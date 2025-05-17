package com.example.nogorok

import android.app.Application
import com.example.nogorok.utils.TokenManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // 전역 초기화
        TokenManager.init(this)
    }
}
