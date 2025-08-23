// app/src/main/java/com/example/nogorok/features/connect/health/HealthMainActivity.kt
package com.example.nogorok.features.connect.health

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.nogorok.R
import com.example.nogorok.features.connect.calendar.CalendarConnectActivity
import com.example.nogorok.features.connect.health.utils.showToast
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.HeartRateSample
import com.example.nogorok.network.dto.HeartRateUploadRequest
import com.example.nogorok.utils.TokenManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import com.example.nogorok.wear.* // ACTION_STRESS_UPDATE, EXTRA_* , StressReceiverService
import kotlin.math.roundToInt

class HealthMainActivity : AppCompatActivity() {

    // ===== 1시간 집계 버킷 =====
    private data class HourBucket(
        val startMs: Long,
        var sumHr: Long = 0,
        var cntHr: Int = 0,
        var sumRmssd: Double = 0.0,
        var cntRmssd: Int = 0,
        var sumEma: Double = 0.0,   // 수신값: 0~1 또는 0~100 (둘 다 지원)
        var cntEma: Int = 0,
        var sumRaw: Double = 0.0,   // 수신값: 0~1 또는 0~100 (둘 다 지원)
        var cntRaw: Int = 0,
    )

    private val buckets = mutableMapOf<Long, HourBucket>() // key = hourStartMs

    private var receiverRegistered = false
    private var pendingUploadJob: Job? = null

    // ===== 워치 → 브로드캐스트 수신 =====
    private val stressReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != ACTION_STRESS_UPDATE) return

            val ema   = intent.getDoubleExtra(EXTRA_STRESS_EMA, Double.NaN)
            val raw   = intent.getDoubleExtra(EXTRA_STRESS_RAW, Double.NaN)
            val hr    = intent.getIntExtra(EXTRA_HR, -1)
            val rmssd = intent.getDoubleExtra(EXTRA_RMSSD, Double.NaN)
            val ts    = intent.getLongExtra(EXTRA_TS, 0L)
            val tsActual = if (ts != 0L) ts else System.currentTimeMillis()

            Log.d("HealthMainActivity", "📥 from Watch: ema=$ema raw=$raw hr=$hr rmssd=$rmssd ts=$tsActual")

            val hStart = hourStart(tsActual)
            val b = buckets.getOrPut(hStart) { HourBucket(hStart) }
            if (hr > 0) { b.sumHr += hr; b.cntHr++ }          // HR=0 제외
            if (!rmssd.isNaN()) { b.sumRmssd += rmssd; b.cntRmssd++ }
            if (!ema.isNaN()) { b.sumEma += ema; b.cntEma++ }
            if (!raw.isNaN()) { b.sumRaw += raw; b.cntRaw++ }

            // 완료 버킷 업로드 시도
            flushCompletedBuckets()
        }
    }

    // ===== 라이프사이클 =====
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_main)

        // Retrofit의 토큰 인터셉터에 provider 연결 (권장)
        RetrofitClient.setTokenProvider { TokenManager.getAccessToken(this) }

        TokenManager.getAccessToken(this)?.let { Log.d("TOKEN_CHECK", "AccessToken: $it") }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)

        btnBack.setOnClickListener { finish() }

        btnNext.setOnClickListener {
            // 워치 데이터 수신 서비스 시작
            StressReceiverService.start(this)
            showToast(this, "워치 연결을 시작했어요. 데이터를 1시간 단위로 집계해 전송합니다.")

            // 온보딩 계속
            startActivity(Intent(this, CalendarConnectActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        if (!receiverRegistered) {
            val filter = IntentFilter(ACTION_STRESS_UPDATE)
            ContextCompat.registerReceiver(
                this, stressReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED
            )
            receiverRegistered = true
            Log.i("HealthMainActivity", "BroadcastReceiver registered for $ACTION_STRESS_UPDATE")
        }
    }

    override fun onStop() {
        super.onStop()
        // 디버그: 현재 진행 중 버킷도 즉시 업로드 시도
        flushAllBucketsNowForDebug()

        // 화면 내려갈 때도 완료된 버킷 업로드
        flushCompletedBuckets()

        if (receiverRegistered) {
            unregisterReceiver(stressReceiver)
            receiverRegistered = false
            Log.i("HealthMainActivity", "BroadcastReceiver unregistered")
        }
    }

    // ===== 집계/업로드 로직 =====

    private fun hourStart(ts: Long): Long = (ts / 3_600_000L) * 3_600_000L

    /** 현재 시간 이전의 버킷(= 완료된 시간대) + 테스트 조건 충족 시 업로드 */
    private fun flushCompletedBuckets() {
        val now = System.currentTimeMillis()
        val currentHourStart = hourStart(now)
        Log.d("UPLOAD", "flush start: now=$now currHour=$currentHourStart buckets=${buckets.keys}")

        val toFlush = buckets.values.filter { b ->
            b.startMs < currentHourStart ||
                    (b.cntHr + b.cntRmssd + b.cntEma + b.cntRaw) >= 10 || // 샘플 10개↑
                    now - b.startMs >= 5 * 60 * 1000L                     // 5분 경과
        }.sortedBy { it.startMs }

        Log.d("UPLOAD", "toFlush.size=${toFlush.size}")
        if (toFlush.isEmpty()) return

        pendingUploadJob?.cancel()
        pendingUploadJob = lifecycleScope.launch(Dispatchers.IO) {
            for (bucket in toFlush) {
                uploadBucketAverage(bucket)
                buckets.remove(bucket.startMs)
            }
        }
    }

    /** 디버그: 현재 진행 중 버킷도 즉시 전송 */
    private fun flushAllBucketsNowForDebug() {
        val toFlush = buckets.values.sortedBy { it.startMs }
        Log.d("UPLOAD", "flushAll DEBUG: size=${toFlush.size}")
        if (toFlush.isEmpty()) return

        pendingUploadJob?.cancel()
        pendingUploadJob = lifecycleScope.launch(Dispatchers.IO) {
            for (b in toFlush) {
                uploadBucketAverage(b)
                buckets.remove(b.startMs)
            }
        }
    }

    /** 0~1 또는 0~100 어떤 입력이 와도 0~100 정수로 안전 변환 */
    private fun toPct0_100(avg: Double): Int {
        if (avg.isNaN()) return 0
        val v = if (avg <= 1.0) avg * 100.0 else avg
        return v.roundToInt().coerceIn(0, 100)
    }

    /** 한 시간 버킷 평균을 계산해 서버로 업로드 */
    private suspend fun uploadBucketAverage(b: HourBucket) {
        Log.d("UPLOAD", "upload trigger bucket=${b.startMs} cnts(hr=${b.cntHr}, rmssd=${b.cntRmssd}, ema=${b.cntEma}, raw=${b.cntRaw})")

        val avgHr = if (b.cntHr > 0) (b.sumHr.toDouble() / b.cntHr).roundToInt() else -1
        val avgRmssd = if (b.cntRmssd > 0) b.sumRmssd / b.cntRmssd else Double.NaN
        val avgEma = if (b.cntEma > 0) b.sumEma / b.cntEma else Double.NaN
        val avgRaw = if (b.cntRaw > 0) b.sumRaw / b.cntRaw else Double.NaN

        val hasAny = (b.cntHr + b.cntRmssd + b.cntEma + b.cntRaw) > 0
        if (!hasAny) {
            Log.w("UPLOAD", "skip: empty bucket")
            return
        }

        try {
            val token = TokenManager.getAccessToken(this@HealthMainActivity)
            if (token.isNullOrBlank()) {
                Log.w("UPLOAD","skip: no token")
                withContext(Dispatchers.Main) { showToast(this@HealthMainActivity, "로그인이 필요합니다.") }
                return
            }

            val email = decodeEmailFromJwt(token)
            if (email.isNullOrBlank()) {
                Log.w("UPLOAD","skip: no email")
                withContext(Dispatchers.Main) { showToast(this@HealthMainActivity, "이메일 정보를 찾을 수 없어요.") }
                return
            }

            val midTs = b.startMs + 30 * 60 * 1000L

            val sample = HeartRateSample(
                timestamp = midTs,
                heartRate = if (avgHr >= 0) avgHr else 0,
                rmssd = if (!avgRmssd.isNaN()) avgRmssd else 0.0,
                stressEma = toPct0_100(avgEma),
                stressRaw = toPct0_100(avgRaw)
            )

            val req = HeartRateUploadRequest(email = email, samples = listOf(sample))

            Log.d("UPLOAD", "POST /api/devices/heartrate email=$email ts=${sample.timestamp} HR=${sample.heartRate} EMA=${sample.stressEma} RAW=${sample.stressRaw}")

            val res = RetrofitClient.deviceApi.uploadHeartRate(req)
            if (res.isSuccessful) {
                Log.i("HealthMainActivity",
                    "✅ [${b.startMs}] 업로드 성공: email=$email, HR=${sample.heartRate}, EMA=${sample.stressEma}, RAW=${sample.stressRaw}, RMSSD=${sample.rmssd}"
                )
            } else {
                Log.w("HealthMainActivity",
                    "❌ [${b.startMs}] 업로드 실패 code=${res.code()} body=${res.errorBody()?.string()}"
                )
            }
        } catch (e: Exception) {
            Log.e("HealthMainActivity", "❌ [${b.startMs}] 업로드 예외: ${e.localizedMessage}", e)
        }
    }

    /** JWT에서 email 추출 (payload.email 없으면 sub 검사) */
    private fun decodeEmailFromJwt(jwt: String?): String? {
        if (jwt.isNullOrBlank()) return null
        return try {
            val payload = jwt.split(".").getOrNull(1) ?: return null
            val json = String(Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP))
            val obj = JSONObject(json)
            obj.optString("email").ifBlank {
                obj.optString("sub").takeIf { it.contains("@") }
            }
        } catch (_: Exception) { null }
    }
}
