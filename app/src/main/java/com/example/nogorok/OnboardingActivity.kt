package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val viewPager = findViewById<ViewPager2>(R.id.onboardingViewPager)
        viewPager.adapter = OnboardingPagerAdapter(this) { // 마지막 페이지에서 콜백
            startActivity(Intent(this, SignupLoginActivity::class.java))
            finish()
        }
        viewPager.isUserInputEnabled = false // 버튼으로만 넘김
    }
}