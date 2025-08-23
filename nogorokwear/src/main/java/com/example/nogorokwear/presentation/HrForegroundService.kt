package com.example.nogorokwear.presentation

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kotlin.math.abs
import kotlin.math.sqrt

private const val TAG = "HR-SVC"
private const val NOTIF_CHANNEL_ID = "hr_tracking"
private const val NOTIF_ID = 1001

// Data Layer 경로/키 (폰과 동일하게 유지)
private const val PATH_STRESS = "/stress"
private const val KEY_EMA   = "stress_ema"
private const val KEY_RAW   = "stress_raw"
private const val KEY_HR    = "hr_bpm"
private const val KEY_RMSSD = "rmssd_ms"
private const val KEY_TS    = "ts"

// 레이트 리밋/버퍼
private var lastSentAt = 0L
private const val SEND_MIN_INTERVAL_MS = 1000L
private const val IBI_BUFFER_MAX = 32
private const val IBI_MIN_FOR_RMSSD = 12
private const val LOG_INTERVAL_MS = 1000L

// IBI 품질
private const val IBI_MIN_MS = 300
private const val IBI_MAX_MS = 1200
private const val IBI_MAX_DELTA_MS = 80

// 스트레스 동적 매핑/EMA
private const val RMSSD_HISTORY_SEC = 60
private const val STRESS_EMA_ALPHA = 0.25f

// 연결 재시도
private const val CONNECT_TIMEOUT_MS = 6000L
private const val RETRY_DELAY_MS = 1500L
private const val MAX_RETRY = 3

class HrForegroundService : Service() {

    private lateinit var hts: HealthTrackingService
    private var hrTracker: HealthTracker? = null

    private var connected = false
    private var retryCount = 0

    private val ibiBuffer = ArrayDeque<Int>()
    private val rmssdHistory = ArrayDeque<Double>()
    private var emaStress: Double? = null
    private var lastLogMs = 0L

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIF_ID, buildOngoingNotification("심박 측정 준비 중…"))
        connectService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        cleanup("onDestroy")
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    // -------- Notification --------
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val ch = NotificationChannel(
                NOTIF_CHANNEL_ID, "심박 추적", NotificationManager.IMPORTANCE_LOW
            ).apply { description = "연속 심박/스트레스 계산 백그라운드 서비스" }
            nm.createNotificationChannel(ch)
        }
    }

    private fun buildOngoingNotification(text: String): Notification =
        NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_more)
            .setContentTitle("노고록 · 워치")
            .setContentText(text)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()

    private fun updateNotification(text: String) {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(NOTIF_ID, buildOngoingNotification(text))
    }

    // -------- Health Tracking --------
    private fun connectService() {
        updateNotification("Samsung Health 연결 중…")

        val connectionListener = object : ConnectionListener {
            override fun onConnectionSuccess() {
                connected = true
                retryCount = 0

                val cap = hts.trackingCapability
                if (!cap.supportHealthTrackerTypes.contains(HealthTrackerType.HEART_RATE_CONTINUOUS)) {
                    updateNotification("연속 심박 미지원 기기")
                    stopSelf(); return
                }

                hrTracker = hts.getHealthTracker(HealthTrackerType.HEART_RATE_CONTINUOUS)
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

                        // IBI 추출 (형식 다양성 대비)
                        val ibiAny: Any? = runCatching {
                            dp.getValue(ValueKey.HeartRateSet.IBI_LIST)
                        }.getOrNull()
                        val ibiArray: IntArray? = when (ibiAny) {
                            is IntArray -> ibiAny
                            is LongArray -> ibiAny.map { it.toInt() }.toIntArray()
                            is List<*> -> ibiAny.mapNotNull { (it as? Number)?.toInt() }.toIntArray()
                            else -> null
                        }?.let { normalizeIbiUnits(it) }

                        val ibiStatusAny: Any? = runCatching {
                            dp.getValue(ValueKey.HeartRateSet.IBI_STATUS_LIST)
                        }.getOrNull()
                        val ibiStatus: IntArray? = when (ibiStatusAny) {
                            is IntArray -> ibiStatusAny
                            is List<*> -> ibiStatusAny.mapNotNull { (it as? Number)?.toInt() }.toIntArray()
                            else -> null
                        }

                        ibiArray?.forEach {
                            ibiBuffer.addLast(it)
                            if (ibiBuffer.size > IBI_BUFFER_MAX) ibiBuffer.removeFirst()
                        }

                        if (now - lastLogMs < LOG_INTERVAL_MS) return
                        lastLogMs = now

                        val filtered = cleanIbi(ibiBuffer.toIntArray(), ibiStatus, hr)
                        val rmssd = if (filtered.size >= IBI_MIN_FOR_RMSSD) computeRmssdMs(filtered) else null

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
                            updateNotification("HR ${hr}bpm · 스트레스 ${"%.0f".format(emaStress)}")
                            sendToPhone(emaStress!!, stressRaw!!, hr, rmssd)
                            Log.d(TAG, "HR=$hr, RMSSD=${"%.1f".format(rmssd)}, RAW=${"%.0f".format(stressRaw)}, EMA=${"%.0f".format(emaStress)} (status=$hrStatus)")
                        } else {
                            updateNotification("HR ${hr}bpm · IBI 수집 중(${ibiBuffer.size})")
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
                connected = false
                if (retryCount++ < MAX_RETRY) {
                    Thread.sleep(RETRY_DELAY_MS)
                    connectService()
                } else {
                    updateNotification("연결 실패: ${e.message}"); stopSelf()
                }
            }

            override fun onConnectionEnded() {
                connected = false
                if (retryCount++ < MAX_RETRY) {
                    Thread.sleep(RETRY_DELAY_MS)
                    connectService()
                } else stopSelf()
            }
        }

        hts = HealthTrackingService(connectionListener, this)
        hts.connectService()

        // 연결 타임아웃 안내(콜백 미도착 대비)
        Thread {
            Thread.sleep(CONNECT_TIMEOUT_MS)
            if (!connected) updateNotification("권한 또는 기기 상태를 확인하세요")
        }.start()
    }

    private fun cleanup(tag: String) {
        runCatching { hrTracker?.unsetEventListener() }
        runCatching { hts.disconnectService() }
        Log.i(TAG, "Cleanup from $tag")
    }

    // -------- Helpers --------
    private fun normalizeIbiUnits(ibi: IntArray): IntArray {
        val median = ibi.sorted()[ibi.size / 2]
        return if (median >= 2000) IntArray(ibi.size) { (ibi[it] / 1000.0).toInt() } else ibi
    }

    private fun cleanIbi(ibiRaw: IntArray, ibiStatus: IntArray?, hrBpm: Int): IntArray {
        if (ibiRaw.isEmpty()) return intArrayOf()
        val ranged = ibiRaw.filter { it in IBI_MIN_MS..IBI_MAX_MS }
        if (ranged.isEmpty()) return intArrayOf()

        val goodStatuses = setOf(0, 1)
        val tmp = ArrayList<Int>(ranged.size)
        var prev: Int? = null
        for (i in ranged.indices) {
            val v = ranged[i]
            if (ibiStatus != null && i < ibiStatus.size && ibiStatus[i] !in goodStatuses) continue
            if (prev != null && abs(v - prev!!) > IBI_MAX_DELTA_MS) { prev = v; continue }
            tmp.add(v); prev = v
        }
        var cleaned = if (tmp.size >= 4) tmp else ranged

        if (hrBpm in 40..200) {
            val meanIbi = cleaned.average()
            val expectedIbi = 60000.0 / hrBpm
            val ratio = meanIbi / expectedIbi
            if (ratio < 0.7 || ratio > 1.3) cleaned = ranged
        }
        return cleaned.takeLast(IBI_BUFFER_MAX).toIntArray()
    }

    private fun computeRmssdMs(ibiMs: IntArray): Double {
        if (ibiMs.size < 2) return Double.NaN
        var sumSq = 0.0; var cnt = 0
        for (i in 1 until ibiMs.size) {
            val d = (ibiMs[i] - ibiMs[i - 1]).toDouble()
            sumSq += d * d; cnt++
        }
        return if (cnt > 0) sqrt(sumSq / cnt) else Double.NaN
    }

    private fun dynamicStressFromHistory(rmssd: Double, history: ArrayDeque<Double>): Double? {
        if (history.size < 15) return null
        val vals = history.sorted()
        fun pct(p: Double): Double {
            val idx = ((vals.size - 1) * p).toInt().coerceIn(0, vals.lastIndex)
            return vals[idx]
        }
        val p20 = pct(0.20); val p80 = pct(0.80)
        if (p80 - p20 < 5.0) return null
        val t = ((rmssd - p20) / (p80 - p20)).coerceIn(0.0, 1.0)
        return (80.0 + (20.0 - 80.0) * t).coerceIn(0.0, 100.0)
    }

    private fun mapRmssdToStressFallback(rmssdMs: Double): Double {
        val lo = 20.0; val hi = 250.0
        val t = ((rmssdMs.coerceIn(lo, hi) - lo) / (hi - lo))
        return (95.0 + (5.0 - 95.0) * t).coerceIn(0.0, 100.0)
    }

    private fun ema(prev: Double, newVal: Double, alpha: Float) =
        prev * (1.0 - alpha) + newVal * alpha

    private fun sendToPhone(stressEma: Double, stressRaw: Double, hr: Int, rmssd: Double) {
        val now = System.currentTimeMillis()
        if (now - lastSentAt < SEND_MIN_INTERVAL_MS) return
        lastSentAt = now

        val put = PutDataMapRequest.create(PATH_STRESS).apply {
            dataMap.putDouble(KEY_EMA,   stressEma)
            dataMap.putDouble(KEY_RAW,   stressRaw)
            dataMap.putInt(KEY_HR,       hr)
            dataMap.putDouble(KEY_RMSSD, rmssd)
            dataMap.putLong(KEY_TS,      now) // 매번 변경되어야 전파됨
        }.asPutDataRequest().setUrgent()

        Wearable.getDataClient(this).putDataItem(put)
            .addOnSuccessListener { Log.d(TAG, "Sent /stress: EMA=${"%.0f".format(stressEma)}") }
            .addOnFailureListener { e -> Log.e(TAG, "Send /stress fail", e) }
    }

    companion object {
        @Volatile private var isStarting = false

        /** 중복 기동 방지 포함 */
        fun start(context: Context) {
            if (isStarting) { Log.d(TAG, "skip start: already starting"); return }
            isStarting = true
            ContextCompat.startForegroundService(
                context, Intent(context, HrForegroundService::class.java)
            )
        }
        fun stop(context: Context) {
            context.stopService(Intent(context, HrForegroundService::class.java))
        }
    }
}
