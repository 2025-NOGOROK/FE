package com.example.nogorok.model

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.samsung.android.sdk.health.data.HealthDataStore

class HealthMainViewModelFactory(
    private val healthDataStore: HealthDataStore,
    private val activity: Activity
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthMainViewModel::class.java)) {
            return HealthMainViewModel(healthDataStore, activity) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}