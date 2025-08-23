package com.example.nogorok

import android.app.Application
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.utils.TokenManager
import com.example.nogorok.wear.StressReceiverService

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // ✅ 매 요청마다 최신 토큰을 가져오도록 설정 (중요)
        RetrofitClient.setTokenProvider { TokenManager.getAccessToken(this) }

        // (선택) 초기 부팅 시점 토큰을 백업용으로도 넣어둘 수 있음
        // RetrofitClient.setAccessToken(TokenManager.getAccessToken(this))

        // ⚠️ Android 13+에서 알림 권한 없으면 FGS 알림 표시가 막혀 예외날 수 있어요.
        // 권한이 있을 때만 서비스 시작하거나, 권한은 Activity에서 먼저 요청하세요.
        if (canStartForegroundSync()) {
            StressReceiverService.start(this)
        }
    }

    private fun canStartForegroundSync(): Boolean {
        if (Build.VERSION.SDK_INT >= 33) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return false
        }
        return true
    }
}
