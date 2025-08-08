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
                title = "바쁜 일상 속 짧은 쉼표",
                subtitle = "바쁜 일상 속 짧은 휴식 타이밍을 제안해 드려요.\n잠깐의 재충전으로 지금 이 순간 숨을 고르세요.",
                imageResId = R.drawable.phone1
            ),
            OnboardingData(
                title = "바쁜 일상 속 짧은 쉼표",
                subtitle = "바쁜 일상 속 짧은 휴식 타이밍을 제안해 드려요.\n잠깐의 재충전으로 지금 이 순간 숨을 고르세요.",
                imageResId = R.drawable.phone2
            ),
            OnboardingData(
                title = "긴 쉼표 일정 추천",
                subtitle = "긴 쉼표 일정의 시나리오를 맞춤 추천해드려요.\n내 주변에서 열리는 행사와 전시를 관람해보세요.",
                imageResId = R.drawable.phone3
            ),
            OnboardingData(
                title = "실시간으로 확인하는 스트레스 지수",
                subtitle = "스마트워치와 연동하여 스트레스 상태를 확인합니다. \n언제든 앱을 켜는 순간 한 눈에 컨디션을 파악하세요.",
                imageResId = R.drawable.phone4
            ),
            OnboardingData(
                title = "주·월간 리포트가 도착했어요",
                subtitle = "주기적으로 스트레스 패턴과 휴식 이력을 분석해 드려요.\n데이터로 보는 변화를 통해 스스로를 격려해보세요.",
                imageResId = R.drawable.phone5
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
