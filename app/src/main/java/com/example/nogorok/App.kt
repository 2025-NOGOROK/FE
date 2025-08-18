package com.example.nogorok

import android.app.Application
import com.example.nogorok.utils.TokenManager
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.wear.StressReceiverService

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        StressReceiverService.start(this)
        val savedToken = TokenManager.getAccessToken(applicationContext)
        RetrofitClient.setAccessToken(savedToken)
    }
}
