package com.example.nogorok.features.auth.splash

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.nogorok.R
import com.example.nogorok.features.auth.login.SignupLoginActivity
import com.example.nogorok.features.auth.onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)

        Handler(Looper.getMainLooper()).postDelayed({
            if (isFirstLaunch) {
                // 온보딩 이후 다시 보지 않도록 저장
                prefs.edit().putBoolean("isFirstLaunch", false).apply()
                startActivity(Intent(this, OnboardingActivity::class.java))
            } else {
                startActivity(Intent(this, SignupLoginActivity::class.java))
            }
            finish()
        }, 2500)
    }
}
