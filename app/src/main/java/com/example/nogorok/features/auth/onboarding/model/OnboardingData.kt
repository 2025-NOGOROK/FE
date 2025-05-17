package com.example.nogorok.features.auth.onboarding.model

import androidx.annotation.DrawableRes

data class OnboardingData(
    val title: String,
    val subtitle: String,
    @DrawableRes val imageResId: Int
)
