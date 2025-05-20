package com.example.nogorok.features.connect.health.service

import android.content.Context
import androidx.work.*
import com.example.nogorok.features.connect.health.viewmodel.HeartRateWorker
import java.util.concurrent.TimeUnit
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkRequest

object HeartRateScheduler {

    fun scheduleHourlyUpload(context: Context) {
        val request = PeriodicWorkRequestBuilder<HeartRateWorker>(
            1, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "HeartRateUploadWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
