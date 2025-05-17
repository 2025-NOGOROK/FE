package com.example.nogorok.features.auth.onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.nogorok.R
import com.example.nogorok.features.auth.onboarding.model.OnboardingData

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: OnboardingPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.onboardingViewPager)

        // ✅ 온보딩 데이터 정의
        val dataList = listOf(
            OnboardingData(
                title = "하루하루 쌓이는 고단함을\n기록하고, 돌아보고, 회복하는 시간",
                subtitle = "노력과 고민의 기록이\n곧 나를 회복시키는 루틴이 됩니다.",
                imageResId = R.drawable.onboarding_1
            ),
            OnboardingData(
                title = "당신의 몸이 말해주는\n스트레스 신호를 기록해요",
                subtitle = "스마트워치와 연동하여\n스트레스 지수를 분석할게요.",
                imageResId = R.drawable.onboarding_2
            ),
            OnboardingData(
                title = "스트레스 지수에 따른 맞춤 루틴,\n오늘도 나를 돌보는 연습을 시작해요",
                subtitle = "하루하루의 감정과 고단함,\n이제 노고록이 함께 기억할게요.",
                imageResId = R.drawable.onboarding_3
            )
        )

        // ✅ 수정된 어댑터 (콜백 제거)
        adapter = OnboardingPagerAdapter(
            activity = this,
            dataList = dataList
        )

        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false // 스와이프 비활성화
    }
}
