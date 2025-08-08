package com.example.nogorok.features.auth.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.nogorok.features.auth.onboarding.model.OnboardingData

class OnboardingPagerAdapter(
    activity: FragmentActivity,
    private val dataList: List<OnboardingData>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = dataList.size

    override fun createFragment(position: Int): Fragment {
        val data   = dataList[position]
        val isLast = position == itemCount - 1
        // ✅ position 인자를 새로 추가
        return OnboardingFragment.newInstance(
            title      = data.title,
            subtitle   = data.subtitle,
            imageResId = data.imageResId,
            isLastPage = isLast,
            position   = position
        )
    }
}
