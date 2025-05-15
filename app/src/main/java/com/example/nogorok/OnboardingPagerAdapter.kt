package com.example.nogorok

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingPagerAdapter(
    activity: FragmentActivity,
    val onComplete: () -> Unit
) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingFragment1()
            1 -> OnboardingFragment2()
            else -> OnboardingFragment3(onComplete)
        }
    }
}