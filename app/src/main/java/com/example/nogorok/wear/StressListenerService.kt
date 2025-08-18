package com.example.nogorok.wear

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable

private const val TAG = "StressReceiver"
private const val PATH_STRESS = "/stress"

const val ACTION_STRESS_UPDATE = "com.example.nogorok.ACTION_STRESS_UPDATE"
const val EXTRA_STRESS_EMA = "stress_ema"
const val EXTRA_STRESS_RAW = "stress_raw"
const val EXTRA_HR = "hr_bpm"
const val EXTRA_RMSSD = "rmssd_ms"
const val EXTRA_TS = "ts"

// 포그라운드 알림
private const val NOTI_CHANNEL_ID = "stress_sync_channel"
private const val NOTI_CHANNEL_NAME = "Stress Sync"
private const val NOTI_ID = 1001

class StressReceiverService :
    Service(),
    DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: start listeners")
        startAsForeground()
        // 런타임 등록
        Wearable.getDataClient(this).addListener(this)
        Wearable.getMessageClient(this).addListener(this)
        Wearable.getCapabilityClient(this)
            .addListener(this, "stress_sync")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 서비스가 죽어도 시스템이 다시 살리도록
        return START_STICKY
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy: remove listeners")
        Wearable.getDataClient(this).removeListener(this)
        Wearable.getMessageClient(this).removeListener(this)
        Wearable.getCapabilityClient(this).removeListener(this)
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // ===== 알림 =====
    private fun startAsForeground() {
        createChannelIfNeeded()
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            // 필요 시 메인 액티비티로 교체
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val noti: Notification = NotificationCompat.Builder(this, NOTI_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_sync) // 앱 아이콘으로 교체 권장
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
            val ch = NotificationChannel(
                NOTI_CHANNEL_ID,
                NOTI_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_MIN
            )
            mgr.createNotificationChannel(ch)
        }
    }

    // ===== Wear listeners =====
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.use { buffer ->
            for (event in buffer) {
                if (event.type != DataEvent.TYPE_CHANGED) continue
                val item = event.dataItem
                if (item.uri.path != PATH_STRESS) continue

                val map = DataMapItem.fromDataItem(item).dataMap
                val ema = map.getDouble("stress_ema")
                val raw = map.getDouble("stress_raw")
                val hr = map.getInt("hr_bpm")
                val rmssd = map.getDouble("rmssd_ms")
                val ts = map.getLong("ts")

                Log.d(TAG, "Recv: ema=$ema raw=$raw hr=$hr rmssd=$rmssd ts=$ts")

                // 액티비티/UI로 브로드캐스트
                val intent = Intent(ACTION_STRESS_UPDATE).apply {
                    putExtra(EXTRA_STRESS_EMA, ema)
                    putExtra(EXTRA_STRESS_RAW, raw)
                    putExtra(EXTRA_HR, hr)
                    putExtra(EXTRA_RMSSD, rmssd)
                    putExtra(EXTRA_TS, ts)
                }
                sendBroadcast(intent)
            }
        }
    }

    override fun onMessageReceived(event: MessageEvent) {
        // 필요 시 메시지 채널 처리
        Log.d(TAG, "Message path=${event.path}, size=${event.data?.size ?: 0}")
    }

    override fun onCapabilityChanged(info: com.google.android.gms.wearable.CapabilityInfo) {
        Log.d(TAG, "Capability changed: ${info.name} -> ${info.nodes.map { it.displayName }}")
    }

    companion object {
        // 앱 어디서든 안전하게 시작
        fun start(context: Context) {
            val intent = Intent(context, StressReceiverService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, StressReceiverService::class.java))
        }
    }
}
