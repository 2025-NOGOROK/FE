package com.example.nogorok.features.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.nogorok.MainActivity
import com.example.nogorok.R
import com.example.nogorok.features.auth.fcm.FCMTokenManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token received: $token")
        FCMTokenManager.saveToken(this, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // 1) 제목/본문(알림 페이로드 우선, 없으면 데이터 페이로드 사용)
        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: getString(R.string.app_name)
        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: ""

        // 2) 라우팅 정보
        val deeplink = remoteMessage.data["deeplink"] // 예: "nogorok://stress/chronic"
        val navigateTo = remoteMessage.data["navigateTo"] // 예: "home" / "schedule" 등 기존 로직
        val autoShortRest = remoteMessage.data["autoShortRest"]?.toBoolean() ?: false
        val date = remoteMessage.data["date"] // "2025-08-22" 등

        // 3) 채널 (없으면 유형별 기본값)
        val channelId = remoteMessage.data["channelId"]
            ?: if (!deeplink.isNullOrBlank()) "stress_alert" else "nogorok_channel"
        val channelName = when (channelId) {
            "stress_alert" -> "스트레스 알림"
            else -> "일반 알림"
        }
        createChannelIfNeeded(channelId, channelName)

        // 4) 클릭 인텐트 분기: 딥링크 > navigateTo > 기본
        val contentIntent: PendingIntent = when {
            !deeplink.isNullOrBlank() -> buildDeepLinkPendingIntent(deeplink)
            !navigateTo.isNullOrBlank() -> buildMainPendingIntent(navigateTo, autoShortRest, date)
            else -> buildMainPendingIntent(null, false, null) // 기존 일반 알림과 동일 (메인 열기)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notification)
    }

    /** 딥링크 PendingIntent: ChronicStressModeFragment 등으로 직행 */
    private fun buildDeepLinkPendingIntent(uri: String): PendingIntent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri)).apply {
            // 기존 액티비티 재사용 + onNewIntent() 유도
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
            setPackage(packageName)
        }
        return PendingIntent.getActivity(
            this,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /** 기존 방식 유지: MainActivity 열고 extras로 분기(home/schedule 등) */
    private fun buildMainPendingIntent(
        navigateTo: String?,
        autoShortRest: Boolean,
        date: String?
    ): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            if (!navigateTo.isNullOrBlank()) putExtra("navigateTo", navigateTo)
            if (autoShortRest) putExtra("autoShortRest", true)
            if (!date.isNullOrBlank()) putExtra("date", date)
        }
        return PendingIntent.getActivity(
            this,
            1002,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createChannelIfNeeded(id: String, name: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(
                NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
    }
}
