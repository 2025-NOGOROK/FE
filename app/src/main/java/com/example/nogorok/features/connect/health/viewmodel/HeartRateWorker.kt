package com.example.nogorok.features.connect.health.viewmodel

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.HeartRateUploadRequest
import com.example.nogorok.utils.TokenManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.nogorok.features.connect.health.utils.HeartRateLocal


class HeartRateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val prefs = applicationContext.getSharedPreferences("HeartRateStorage", Context.MODE_PRIVATE)
            val json = prefs.getString("unsentHeartRates", null) ?: return@withContext Result.success()

            val type = object : TypeToken<List<HeartRateLocal>>() {}.type
            val heartRates: List<HeartRateLocal> = Gson().fromJson(json, type)

            val email = TokenManager.getEmail(applicationContext)
                ?: return@withContext Result.failure()

            for (hr in heartRates) {
                val request = HeartRateUploadRequest(
                    email = email,
                    startTime = "2025-05-20T${hr.startTime}:00", // 실제 프로젝트에서는 저장 시점 기반으로 조정
                    endTime = "2025-05-20T${hr.endTime}:00",
                    count = hr.count,
                    min = hr.min,
                    max = hr.max,
                    avg = hr.avg,
                    stress = hr.stress
                )

                val response = RetrofitClient.healthApi.uploadHeartRate(request)
                if (!response.isSuccessful) {
                    Log.e("HeartRateUpload", "전송 실패: ${response.code()}")
                    return@withContext Result.retry()
                }
            }

            prefs.edit().remove("unsentHeartRates").apply()
            Log.i("HeartRateUpload", "모든 데이터 업로드 완료")
            Result.success()

        } catch (e: Exception) {
            Log.e("HeartRateUpload", "예외 발생: ${e.message}")
            Result.failure()
        }
    }
}


