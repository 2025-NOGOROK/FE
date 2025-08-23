package com.example.nogorok.features.connect.health.worker

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.HeartRateSample
import com.example.nogorok.network.dto.HeartRateUploadRequest
import com.example.nogorok.features.connect.health.store.AggregationStore
import com.example.nogorok.features.connect.health.store.AggregationStore.toAverages
import com.example.nogorok.utils.TokenManager
import org.json.JSONObject

class HourlyUploadWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val ctx = applicationContext
        val token = TokenManager.getAccessToken(ctx)
        if (token.isNullOrBlank()) {
            Log.w("HourlyUploadWorker", "no token; skip")
            return Result.success()
        }
        RetrofitClient.setAccessToken(token)

        val email = decodeEmail(token) ?: run {
            Log.w("HourlyUploadWorker", "no email; skip")
            return Result.success()
        }

        val completed = AggregationStore.readCompleted(ctx)
        if (completed.isEmpty()) return Result.success()

        var anyFailure = false
        for (b in completed) {
            val avg = toAverages(b)
            val sample = HeartRateSample(
                timestamp = avg.tsMid,
                heartRate = avg.hr,
                rmssd = avg.rmssd,
                stressEma = avg.emaPct,
                stressRaw = avg.rawPct
            )
            val req = HeartRateUploadRequest(email, listOf(sample))
            try {
                val res = RetrofitClient.deviceApi.uploadHeartRate(req)
                if (res.isSuccessful) {
                    AggregationStore.remove(ctx, b.startMs)
                    Log.i("HourlyUploadWorker", "uploaded hour=${b.startMs}")
                } else {
                    anyFailure = true
                    Log.w("HourlyUploadWorker", "fail code=${res.code()} body=${res.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                anyFailure = true
                Log.e("HourlyUploadWorker", "exception: ${e.localizedMessage}", e)
            }
        }
        return if (anyFailure) Result.retry() else Result.success()
    }

    private fun decodeEmail(jwt: String?): String? {
        if (jwt.isNullOrBlank()) return null
        return try {
            val payload = jwt.split(".").getOrNull(1) ?: return null
            val json = String(Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP))
            val obj = JSONObject(json)
            obj.optString("email").ifBlank { obj.optString("sub").takeIf { it.contains("@") } }
        } catch (_: Exception) { null }
    }
}
