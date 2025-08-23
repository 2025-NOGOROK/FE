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

        // 1) Retrofit/OkHttp 초기화 (전역 인터셉터에 Context 주입)
        RetrofitClient.init(applicationContext)

        // 2) 매 요청마다 최신 액세스 토큰을 읽도록 공급자 연결
        RetrofitClient.setTokenProvider {
            TokenManager.getAccessToken(applicationContext)
        }

        // (선택) 초기 백업 토큰 주입이 필요하면 사용
        // RetrofitClient.setAccessToken(TokenManager.getAccessToken(applicationContext))

        // 3) 알림 권한 확인 후 포그라운드 동기화 서비스 시작 (필요 시)
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
