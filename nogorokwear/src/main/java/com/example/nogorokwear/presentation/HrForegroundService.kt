package com.example.nogorokwear.presentation


import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kotlin.math.abs
import kotlin.math.sqrt
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

private var lastSentAt = 0L
private const val SEND_MIN_INTERVAL_MS = 1000L
private const val PATH_STRESS = "/stress"

private const val TAG = "HR-SVC"
private const val NOTIF_CHANNEL_ID = "hr_tracking"
private const val NOTIF_ID = 1001

// 슬라이딩 윈도우/레이트 리밋
private const val IBI_BUFFER_MAX = 32
private const val IBI_MIN_FOR_RMSSD = 12
private const val LOG_INTERVAL_MS = 1000L

// IBI 품질 기준
private const val IBI_MIN_MS = 300
private const val IBI_MAX_MS = 1200
private const val IBI_MAX_DELTA_MS = 80

// 동적 매핑
private const val RMSSD_HISTORY_SEC = 60
private const val STRESS_EMA_ALPHA = 0.25f

private const val CONNECT_TIMEOUT_MS = 6000L
private const val RETRY_DELAY_MS = 1500L
private const val MAX_RETRY = 3

class HrForegroundService : Service() {

    private lateinit var trackingService: HealthTrackingService
    private var hrTracker: HealthTracker? = null
    private val handler = Handler(Looper.getMainLooper())

    private var connected = false
    private var retryCount = 0

    private val ibiBuffer = ArrayDeque<Int>()
    private val rmssdHistory = ArrayDeque<Double>()
    private var emaStress: Double? = null
    private var lastLogMs = 0L

    override fun onCreate() {
        super.onCreate()

        // Android 13+ 알림 권한 필수 (Activity에서 요청 완료 전제지만, 여기서도 보수적으로 확인)
        if (!canPostNotifications()) {
            Log.w(TAG, "POST_NOTIFICATIONS not granted. Stopping service.")
            stopSelf()
            return
        }

        createNotificationChannel()
        startForeground(NOTIF_ID, buildOngoingNotification("심박 측정 준비 중…"))
        connectService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        cleanup("onDestroy")
        super.onDestroy()
    }

    // ---------- Permission ----------
    private fun canPostNotifications(): Boolean {
        return if (Build.VERSION.SDK_INT >= 33) {
            ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    // ---------- Notification ----------
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val ch = NotificationChannel(
                NOTIF_CHANNEL_ID,
                "심박 추적",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "연속 심박/스트레스 계산을 위한 백그라운드 서비스"
            }
            nm.createNotificationChannel(ch)
        }
    }

    private fun buildOngoingNotification(text: String): Notification {
        val builder = NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_more)
            .setContentTitle("노고록 심박 추적")
            .setContentText(text)
            .setOnlyAlertOnce(true)
            .setOngoing(true)

        if (Build.VERSION.SDK_INT >= 34) {
            builder.setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
        }
        return builder.build()
    }

    private fun updateNotification(text: String) {
        // Android 13+ 에서는 알림 퍼미션이 없으면 notify() 호출 금지
        if (Build.VERSION.SDK_INT >= 33) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                Log.w(TAG, "POST_NOTIFICATIONS not granted; skip notify()")
                return
            }
        }

        // 여기까지 오면 안전
        NotificationManagerCompat.from(this)
            .notify(NOTIF_ID, buildOngoingNotification(text))
    }


    // ---------- Health Tracking ----------
    private fun connectService() {
        updateNotification("Samsung Health 연결 중…")

        val connectionListener = object : ConnectionListener {
            override fun onConnectionSuccess() {
                handler.removeCallbacksAndMessages(null)
                connected = true
                retryCount = 0

                val cap = trackingService.trackingCapability
                if (!cap.supportHealthTrackerTypes.contains(HealthTrackerType.HEART_RATE_CONTINUOUS)) {
                    updateNotification("연속 심박 미지원 기기")
                    stopSelf()
                    return
                }

                hrTracker = trackingService.getHealthTracker(HealthTrackerType.HEART_RATE_CONTINUOUS)
                runCatching { hrTracker?.unsetEventListener() }

                val listener = object : HealthTracker.TrackerEventListener {
                    override fun onDataReceived(data: List<DataPoint>) {
                        if (data.isEmpty()) return
                        val now = System.currentTimeMillis()
                        val dp = data.last()

                        val hr: Int = dp.getValue(ValueKey.HeartRateSet.HEART_RATE)
                        val hrStatus: Int? = runCatching {
                            dp.getValue(ValueKey.HeartRateSet.HEART_RATE_STATUS)
                        }.getOrNull()

                        // IBI 추출
                        val ibiRawAny: Any? = runCatching {
                            dp.getValue(ValueKey.HeartRateSet.IBI_LIST)
                        }.getOrNull()

                        val ibiListOrNull: IntArray? = when (ibiRawAny) {
                            is IntArray -> ibiRawAny
                            is LongArray -> ibiRawAny.map { it.toInt() }.toIntArray()
                            is List<*> -> {
                                val ints = ibiRawAny.mapNotNull { (it as? Number)?.toInt() }
                                if (ints.isNotEmpty()) ints.toIntArray() else null
                            }
                            else -> null
                        }

                        // 단위 보정 및 버퍼 누적
                        val normalizedIbi: IntArray? = ibiListOrNull
                            ?.takeIf { it.isNotEmpty() }
                            ?.let { normalizeIbiUnits(it) }

                        val ibiStatusAny: Any? = runCatching {
                            dp.getValue(ValueKey.HeartRateSet.IBI_STATUS_LIST)
                        }.getOrNull()

                        val ibiStatus: IntArray? = when (ibiStatusAny) {
                            is IntArray -> ibiStatusAny
                            is List<*> -> {
                                val ints = ibiStatusAny.mapNotNull { (it as? Number)?.toInt() }
                                if (ints.isNotEmpty()) ints.toIntArray() else null
                            }
                            else -> null
                        }

                        if (normalizedIbi != null) {
                            for (ibi in normalizedIbi) {
                                ibiBuffer.addLast(ibi)
                                if (ibiBuffer.size > IBI_BUFFER_MAX) ibiBuffer.removeFirst()
                            }
                        }

                        if (now - lastLogMs < LOG_INTERVAL_MS) return
                        lastLogMs = now

                        val filtered = cleanIbi(ibiBuffer.toIntArray(), ibiStatus, hr)
                        val rmssd: Double? =
                            if (filtered.size >= IBI_MIN_FOR_RMSSD) computeRmssdMs(filtered) else null

                        var stressRaw: Double? = null
                        if (rmssd != null && !rmssd.isNaN()) {
                            rmssdHistory.addLast(rmssd)
                            if (rmssdHistory.size > RMSSD_HISTORY_SEC) rmssdHistory.removeFirst()

                            stressRaw = dynamicStressFromHistory(rmssd, rmssdHistory)
                                ?: mapRmssdToStressFallback(rmssd)

                            emaStress = if (emaStress == null) stressRaw
                            else ema(emaStress!!, stressRaw, STRESS_EMA_ALPHA)
                        }

                        if (rmssd != null && emaStress != null) {
                            Log.d(
                                TAG,
                                "HR=${hr} bpm, RMSSD=${"%.1f".format(rmssd)} ms, " +
                                        "Stress(raw)=${"%.0f".format(stressRaw!!)}, Stress(ema)=${"%.0f".format(emaStress)} " +
                                        "(HR_STATUS=$hrStatus, IBI_BUF=${ibiBuffer.size}, IBI_FILT=${filtered.size})"
                            )
                            updateNotification("HR ${hr}bpm · 스트레스 ${"%.0f".format(emaStress)}")
                            sendStressToPhone(emaStress!!, stressRaw!!, hr, rmssd)
                        } else {
                            Log.d(TAG, "HR=${hr} bpm (HR_STATUS=$hrStatus) — IBI 대기 buf=${ibiBuffer.size}")
                            updateNotification("HR ${hr}bpm · IBI 수집 중…(${ibiBuffer.size})")
                        }
                    }

                    override fun onFlushCompleted() {}
                    override fun onError(error: HealthTracker.TrackerError) {
                        Log.e(TAG, "Tracker error: $error")
                        updateNotification("트래커 오류: $error")
                    }
                }

                hrTracker?.setEventListener(listener)
                updateNotification("연속 심박 측정 중…")
            }

            override fun onConnectionFailed(e: HealthTrackerException) {
                handler.removeCallbacksAndMessages(null)
                connected = false
                if (retryCount < MAX_RETRY) {
                    retryCount++
                    handler.postDelayed({ connectService() }, RETRY_DELAY_MS)
                } else stopSelf()
            }

            override fun onConnectionEnded() {
                connected = false
                if (retryCount < MAX_RETRY) {
                    retryCount++
                    handler.postDelayed({ connectService() }, RETRY_DELAY_MS)
                }
            }
        }

        // Service 컨텍스트로 바인딩 → Activity 수명과 분리
        trackingService = HealthTrackingService(connectionListener, this)
        trackingService.connectService()

        handler.postDelayed({
            if (!connected) updateNotification("6초 내 콜백 없음 — 권한/앱 상태 확인")
        }, CONNECT_TIMEOUT_MS)
    }

    private fun cleanup(tag: String) {
        runCatching { hrTracker?.unsetEventListener() }
        runCatching { if (::trackingService.isInitialized) trackingService.disconnectService() }
        handler.removeCallbacksAndMessages(null)
        Log.i(TAG, "Cleanup from $tag")
    }

    // -------- Utils --------

    // μs → ms 자동 보정: 중앙값이 2000 이상이면 μs로 판단해 1000으로 나눔
    private fun normalizeIbiUnits(ibi: IntArray): IntArray {
        val sorted = ibi.sorted()
        val median = sorted[sorted.size / 2]
        return if (median >= 2000) {
            IntArray(ibi.size) { idx -> (ibi[idx] / 1000.0).toInt() }
        } else {
            ibi
        }
    }

    private fun cleanIbi(ibiRaw: IntArray, ibiStatus: IntArray?, hrBpm: Int): IntArray {
        if (ibiRaw.isEmpty()) return intArrayOf()

        // 1) 범위 필터
        val ranged = ibiRaw.filter { it in IBI_MIN_MS..IBI_MAX_MS }
        if (ranged.isEmpty()) return intArrayOf()

        // 2) 상태 + ΔIBI 필터
        val goodStatuses = setOf(0, 1)
        val tmp = ArrayList<Int>(ranged.size)
        var prev: Int? = null
        for (i in ranged.indices) {
            val v = ranged[i]
            if (ibiStatus != null && i < ibiStatus.size && ibiStatus[i] !in goodStatuses) continue
            if (prev != null && abs(v - prev!!) > IBI_MAX_DELTA_MS) {
                prev = v; continue
            }
            tmp.add(v); prev = v
        }
        var cleaned = if (tmp.size >= 4) tmp else ranged

        // 3) HR과 평균 IBI 상식 체크 (역수 관계)
        if (hrBpm in 40..200) {
            val meanIbi = cleaned.average()
            val expectedIbi = 60000.0 / hrBpm
            val ratio = meanIbi / expectedIbi
            if (ratio < 0.7 || ratio > 1.3) {
                cleaned = ranged
            }
        }

        return cleaned.takeLast(IBI_BUFFER_MAX).toIntArray()
    }

    private fun computeRmssdMs(ibiMs: IntArray): Double {
        if (ibiMs.size < 2) return Double.NaN
        var sumSq = 0.0
        var count = 0
        for (i in 1 until ibiMs.size) {
            val diff = (ibiMs[i] - ibiMs[i - 1]).toDouble()
            sumSq += diff * diff
            count++
        }
        return if (count > 0) sqrt(sumSq / count) else Double.NaN
    }

    private fun dynamicStressFromHistory(rmssd: Double, history: ArrayDeque<Double>): Double? {
        if (history.size < 15) return null
        val vals = history.sorted()
        fun pct(p: Double): Double {
            val idx = ((vals.size - 1) * p).toInt().coerceIn(0, vals.lastIndex)
            return vals[idx]
        }
        val p20 = pct(0.20)
        val p80 = pct(0.80)
        if (p80 - p20 < 5.0) return null
        val t = ((rmssd - p20) / (p80 - p20)).coerceIn(0.0, 1.0)
        return (80.0 + (20.0 - 80.0) * t).coerceIn(0.0, 100.0)
    }

    private fun mapRmssdToStressFallback(rmssdMs: Double): Double {
        val lo = 20.0
        val hi = 250.0
        val clamped = rmssdMs.coerceIn(lo, hi)
        val t = (clamped - lo) / (hi - lo)
        return (95.0 + (5.0 - 95.0) * t).coerceIn(0.0, 100.0)
    }

    private fun ema(prev: Double, newVal: Double, alpha: Float): Double {
        return prev * (1.0 - alpha) + newVal * alpha
    }

    private fun sendStressToPhone(stressEma: Double, stressRaw: Double, hr: Int, rmssd: Double) {
        val now = System.currentTimeMillis()
        if (now - lastSentAt < SEND_MIN_INTERVAL_MS) return
        lastSentAt = now

        val putReq = PutDataMapRequest.create(PATH_STRESS).apply {
            dataMap.putDouble("stress_ema", stressEma)
            dataMap.putDouble("stress_raw", stressRaw)
            dataMap.putInt("hr_bpm", hr)
            dataMap.putDouble("rmssd_ms", rmssd)
            dataMap.putLong("ts", now) // 매번 변경되어야 DataItem이 전파됨(중요)
        }.asPutDataRequest().setUrgent() // 필요 시 urgent

        val dataClient: DataClient = Wearable.getDataClient(this)
        dataClient.putDataItem(putReq)
            .addOnSuccessListener { Log.d(TAG, "Sent stress to phone: $stressEma") }
            .addOnFailureListener { e -> Log.e(TAG, "Failed to send stress", e) }
    }
}
