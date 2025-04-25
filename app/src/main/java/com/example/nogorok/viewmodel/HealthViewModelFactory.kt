/*
 * Copyright (C) 2024 Samsung Electronics Co., Ltd. All rights reserved
 */
package com.example.nogorok.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.samsung.android.sdk.health.data.HealthDataService

class HealthViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        HealthMainViewModel::class.java ->
            HealthMainViewModel(HealthDataService.getStore(context), context as Activity)

        StepViewModel::class.java ->
            StepViewModel(HealthDataService.getStore(context), context as Activity)

        NutritionViewModel::class.java ->
            NutritionViewModel(HealthDataService.getStore(context), context as Activity)

        HeartRateViewModel::class.java ->
            HeartRateViewModel(HealthDataService.getStore(context), context as Activity)

        SleepViewModel::class.java ->
            SleepViewModel(HealthDataService.getStore(context), context as Activity)

        ChooseFoodViewModel::class.java ->
            ChooseFoodViewModel(HealthDataService.getStore(context), context as Activity)

        UpdateFoodViewModel::class.java ->
            UpdateFoodViewModel(HealthDataService.getStore(context), context as Activity)

        else -> throw IllegalArgumentException("Unknown ViewModel class")
    } as T
}
