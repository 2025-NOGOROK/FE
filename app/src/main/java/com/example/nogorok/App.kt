package com.example.nogorok

import android.app.Application
import com.example.nogorok.utils.TokenManager
import com.example.nogorok.network.RetrofitClient

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        val savedToken = TokenManager.getAccessToken(applicationContext)
        RetrofitClient.setAccessToken(savedToken)
    }
}
