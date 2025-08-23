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
        var sumEma: Double = 0.0,   // 0.0 ~ 1.0 샘플 합
        var cntEma: Int = 0,
        var sumRaw: Double = 0.0,   // 0.0 ~ 1.0 샘플 합
        var cntRaw: Int = 0,
    )

    private val buckets = mutableMapOf<Long, HourBucket>() // key = hourStartMs

    private var receiverRegistered = false
    private var pendingUploadJob: Job? = null

    // ===== 워치 → 브로드캐스트 수신 =====
    private val stressReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != ACTION_STRESS_UPDATE) return

            val ema   = intent.getDoubleExtra(EXTRA_STRESS_EMA, Double.NaN)   // 0.0~1.0
            val raw   = intent.getDoubleExtra(EXTRA_STRESS_RAW, Double.NaN)   // 0.0~1.0
            val hr    = intent.getIntExtra(EXTRA_HR, -1)
            val rmssd = intent.getDoubleExtra(EXTRA_RMSSD, Double.NaN)
            val ts    = intent.getLongExtra(EXTRA_TS, 0L)
            val tsActual = if (ts != 0L) ts else System.currentTimeMillis()

            Log.d("HealthMainActivity", "📥 from Watch: ema=$ema raw=$raw hr=$hr rmssd=$rmssd ts=$tsActual")

            // 1) 버킷에 적재
            val hStart = hourStart(tsActual)
            val b = buckets.getOrPut(hStart) { HourBucket(hStart) }
            if (hr >= 0) { b.sumHr += hr; b.cntHr++ }
            if (!rmssd.isNaN()) { b.sumRmssd += rmssd; b.cntRmssd++ }
            if (!ema.isNaN()) { b.sumEma += ema; b.cntEma++ }
            if (!raw.isNaN()) { b.sumRaw += raw; b.cntRaw++ }

            // 2) 완료된 버킷 업로드 시도 (현재 진행 중인 시각의 버킷 제외)
            flushCompletedBuckets()
        }
    }

    // ===== 라이프사이클 =====
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_main)

        // 토큰 로그(서버 인증 확인)
        TokenManager.getAccessToken(this)?.let { Log.d("TOKEN_CHECK", "AccessToken: $it") }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)

        btnBack.setOnClickListener { finish() }

        btnNext.setOnClickListener {
            // 워치 데이터 수신 서비스 시작 (중복 가드 내장)
            StressReceiverService.start(this)
            showToast(this, "워치 연결을 시작했어요. 데이터를 1시간 단위로 집계해 전송합니다.")

            // 온보딩 흐름 유지: 바로 다음 화면으로 이동(집계/업로드는 백그라운드에서 계속)
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
        }
    }

    override fun onStop() {
        super.onStop()
        // 화면 내려갈 때도 완료된 버킷이 있으면 업로드
        flushCompletedBuckets()
        if (receiverRegistered) {
            unregisterReceiver(stressReceiver)
            receiverRegistered = false
        }
    }

    // ===== 집계/업로드 로직 =====

    private fun hourStart(ts: Long): Long = (ts / 3_600_000L) * 3_600_000L

    /** 현재 시간 이전의 버킷(= 완료된 시간대)만 업로드하고 맵에서 제거 */
    private fun flushCompletedBuckets() {
        val now = System.currentTimeMillis()
        val currentHourStart = hourStart(now)

        val toFlush = buckets.values
            .filter { it.startMs < currentHourStart }
            .sortedBy { it.startMs }
        if (toFlush.isEmpty()) return

        pendingUploadJob?.cancel()
        pendingUploadJob = lifecycleScope.launch(Dispatchers.IO) {
            for (bucket in toFlush) {
                uploadBucketAverage(bucket)
                // 성공/실패 무관하게 제거(필요 시 실패 보존 로직 추가)
                buckets.remove(bucket.startMs)
            }
        }
    }

    /**
     * 한 시간 버킷 평균을 계산해 서버로 업로드
     * - timestamp: 해당 시간대의 가운데(시작+30분)
     * - stressEma / stressRaw: 0~1 → 0~100 정수(반올림, 경계 보정)
     * - 서버 스펙: POST /api/devices/heartrate  { email, samples:[{...}] }
     */
    private suspend fun uploadBucketAverage(b: HourBucket) {
        val avgHr = if (b.cntHr > 0) (b.sumHr.toDouble() / b.cntHr).roundToInt() else -1
        val avgRmssd = if (b.cntRmssd > 0) b.sumRmssd / b.cntRmssd else Double.NaN
        val avgEma = if (b.cntEma > 0) b.sumEma / b.cntEma else Double.NaN   // 0.0~1.0
        val avgRaw = if (b.cntRaw > 0) b.sumRaw / b.cntRaw else Double.NaN   // 0.0~1.0

        val hasAny = (b.cntHr + b.cntRmssd + b.cntEma + b.cntRaw) > 0
        if (!hasAny) return

        try {
            val token = TokenManager.getAccessToken(this@HealthMainActivity)
            if (token.isNullOrBlank()) {
                withContext(Dispatchers.Main) { showToast(this@HealthMainActivity, "로그인이 필요합니다.") }
                return
            }

            val email = decodeEmailFromJwt(token)
            if (email.isNullOrBlank()) {
                withContext(Dispatchers.Main) { showToast(this@HealthMainActivity, "이메일 정보를 찾을 수 없어요.") }
                return
            }

            val midTs = b.startMs + 30 * 60 * 1000L

            // 0~1 → 0~100 정수
            val emaPct = if (!avgEma.isNaN()) (avgEma * 100).roundToInt().coerceIn(0, 100) else 0
            val rawPct = if (!avgRaw.isNaN()) (avgRaw * 100).roundToInt().coerceIn(0, 100) else 0

            val sample = HeartRateSample(
                timestamp = midTs,
                heartRate = if (avgHr >= 0) avgHr else 0,
                rmssd = if (!avgRmssd.isNaN()) avgRmssd else 0.0,
                stressEma = emaPct,
                stressRaw = rawPct
            )

            val req = HeartRateUploadRequest(email = email, samples = listOf(sample))

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
