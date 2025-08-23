package com.example.nogorok.wear

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

private const val TAG = "StressReceiver"
private const val CAPABILITY_NAME = "stress_sync"
private const val PATH_STRESS = "/stress"

// 앱 내부 브로드캐스트 액션/엑스트라 키
const val ACTION_STRESS_UPDATE = "com.example.nogorok.ACTION_STRESS_UPDATE"
const val EXTRA_STRESS_EMA = "stress_ema"
const val EXTRA_STRESS_RAW = "stress_raw"
const val EXTRA_HR = "hr_bpm"
const val EXTRA_RMSSD = "rmssd_ms"
const val EXTRA_TS = "ts"

// 포그라운드 알림 채널
private const val NOTI_CHANNEL_ID = "stress_sync_channel"
private const val NOTI_CHANNEL_NAME = "Stress Sync"
private const val NOTI_ID = 1001

class StressReceiverService :
    Service(),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    companion object {
        @Volatile private var isStarting = false

        /** 안전하게 포그라운드 서비스 시작 (중복 기동 방지) */
        fun start(context: Context) {
            if (isStarting) {
                Log.d(TAG, "skip start: already starting")
                return
            }
            isStarting = true
            ContextCompat.startForegroundService(
                context, Intent(context, StressReceiverService::class.java)
            )
        }

        /** 서비스 중지 */
        fun stop(context: Context) {
            context.stopService(Intent(context, StressReceiverService::class.java))
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: registering Wear listeners")
        startAsForeground()

        // Wear OS Data Layer 리스너 등록
        Wearable.getDataClient(this).addListener(this)
        Wearable.getMessageClient(this).addListener(this)
        Wearable.getCapabilityClient(this).addListener(this, CAPABILITY_NAME)

        // 1시간마다 자동 업로드 워커 예약 (중복 예약 방지 + 정시 정렬)
        ensureHourlyWorker()

        // 중복 기동 플래그 해제
        isStarting = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 서비스가 종료돼도 시스템이 재시작하도록
        return START_STICKY
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy: unregistering Wear listeners")
        // 리스너 해제
        Wearable.getDataClient(this).removeListener(this)
        Wearable.getMessageClient(this).removeListener(this)
        Wearable.getCapabilityClient(this).removeListener(this)

        // 포그라운드 해제
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // -------------------- Foreground --------------------

    private fun startAsForeground() {
        createChannelIfNeeded()

        // 앱 실행 인텐트 (런처 인텐트가 없을 경우 대비)
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent ?: Intent().setPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val noti: Notification = NotificationCompat.Builder(this, NOTI_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_sync) // 필요 시 앱 아이콘으로 교체
            .setContentTitle("NOGOROK 연결")
            .setContentText("워치 ↔ 폰 데이터 동기화 중")
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTI_ID, noti)
    }

    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                NOTI_CHANNEL_ID,
                NOTI_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_MIN
            )
            mgr.createNotificationChannel(channel)
        }
    }

    // -------------------- Wear Listeners --------------------

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.use { buffer ->
            for (event in buffer) {
                if (event.type != DataEvent.TYPE_CHANGED) continue
                val item = event.dataItem
                if (item.uri.path != PATH_STRESS) continue

                val map = DataMapItem.fromDataItem(item).dataMap

                // 키 존재 여부를 확인해 안전하게 파싱
                val ema = if (map.containsKey(EXTRA_STRESS_EMA)) map.getDouble(EXTRA_STRESS_EMA) else Double.NaN
                val raw = if (map.containsKey(EXTRA_STRESS_RAW)) map.getDouble(EXTRA_STRESS_RAW) else Double.NaN
                val hr = if (map.containsKey(EXTRA_HR)) map.getInt(EXTRA_HR) else -1
                val rmssd = if (map.containsKey(EXTRA_RMSSD)) map.getDouble(EXTRA_RMSSD) else Double.NaN
                val ts = if (map.containsKey(EXTRA_TS)) map.getLong(EXTRA_TS) else 0L

                Log.d(TAG, "Recv: ema=$ema raw=$raw hr=$hr rmssd=$rmssd ts=$ts")

                // 앱 내부로만 브로드캐스트
                val broadcast = Intent(ACTION_STRESS_UPDATE).apply {
                    putExtra(EXTRA_STRESS_EMA, ema)
                    putExtra(EXTRA_STRESS_RAW, raw)
                    putExtra(EXTRA_HR, hr)
                    putExtra(EXTRA_RMSSD, rmssd)
                    putExtra(EXTRA_TS, ts)
                    setPackage(packageName) // 내 앱으로만 전달
                }
                sendBroadcast(broadcast)
            }
        }
    }

    override fun onMessageReceived(event: MessageEvent) {
        Log.d(TAG, "Message received: path=${event.path}, size=${event.data?.size ?: 0}")
    }

    override fun onCapabilityChanged(info: CapabilityInfo) {
        Log.d(TAG, "Capability changed: ${info.name} -> ${info.nodes.map { it.displayName }}")
    }

    // -------------------- WorkManager (1시간마다 업로드) --------------------

    /** 1시간 주기 업로드 워커 예약. 이미 있으면 유지(KEEP) */
    private fun ensureHourlyWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // 다음 정시까지 초기 지연(정시 정렬)
        val now = System.currentTimeMillis()
        val nextHourStart = ((now / 3_600_000L) + 1) * 3_600_000L
        val initialDelayMs = (nextHourStart - now).coerceAtLeast(0L)

        val request = PeriodicWorkRequestBuilder<
                com.example.nogorok.features.connect.health.worker.HourlyUploadWorker
                >(1, TimeUnit.HOURS)
            .setInitialDelay(initialDelayMs, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "hourly_hr_upload",
            ExistingPeriodicWorkPolicy.KEEP,  // 이미 있으면 유지 (중복 등록 방지)
            request
        )
    }

    /** 업로드 작업 해제 (원할 때 호출) */
    private fun cancelHourlyWorker() {
        WorkManager.getInstance(this).cancelUniqueWork("hourly_hr_upload")
    }
}
