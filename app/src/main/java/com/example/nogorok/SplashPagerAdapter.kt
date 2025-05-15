package com.example.nogorok

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.nogorok.fragment.SignupLoginFragment

class SplashPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 4 // 총 페이지 수

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SplashFragment.newInstance(
                "하루하루 쌓이는 고단함을 기록하고, 돌아보고, 회복하는 시간",
                android.R.drawable.ic_dialog_info
            )
            1 -> SplashFragment.newInstance(
                "당신의 몸이 말해주는 스트레스 신호를 기록해요",
                android.R.drawable.ic_dialog_email
            )
            2 -> SplashFragment.newInstance(
                "스트레스 지수에 따른 맞춤 루틴, 오늘도 나를 돌보는 연습을 시작해요",
                android.R.drawable.ic_dialog_map
            )
            else -> SignupLoginFragment() // 마지막 페이지는 로그인/회원가입 화면
        }
    }
}