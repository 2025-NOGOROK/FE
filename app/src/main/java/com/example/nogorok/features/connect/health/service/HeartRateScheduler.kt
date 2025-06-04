// HeartRateScheduler.kt
package com.example.nogorok.features.connect.health.service

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit
import com.example.nogorok.features.connect.health.viewmodel.HeartRateWorker

object HeartRateScheduler {
    fun scheduleHourlyUpload(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadRequest = PeriodicWorkRequestBuilder<HeartRateWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "HeartRateUploadWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            uploadRequest
        )
    }
}
