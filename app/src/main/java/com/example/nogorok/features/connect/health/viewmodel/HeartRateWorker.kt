package com.example.nogorok.features.connect.health.viewmodel

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.HeartRateUploadRequest
import com.example.nogorok.utils.TokenManager
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HeartRateWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val MAX_RETRIES = 5
        private const val RETRY_INTERVAL_MS = 60 * 60 * 1000L // 1시간
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d("HeartRateWorker", "⏰ WorkManager 실행 시작")

        val email = TokenManager.getEmail(context)
        val token = TokenManager.getAccessToken(context)

        if (email.isNullOrEmpty() || token.isNullOrEmpty()) {
            Log.e("HeartRateWorker", "❌ 이메일 또는 토큰이 없음")
            return@withContext Result.failure()
        }

        repeat(MAX_RETRIES) { attempt ->
            Log.d("HeartRateWorker", "🔄 데이터 조회 시도: ${attempt + 1}")

            val request = getLatestHeartRateRequest(email)
            if (request != null) {
                Log.d("HeartRateWorker", "📦 전송할 데이터: $request")

                val response = RetrofitClient.healthApi.uploadHeartRate(request)

                return@withContext if (response.isSuccessful) {
                    Log.d("HeartRateWorker", "✅ 서버 업로드 성공")
                    Result.success()
                } else {
                    Log.e("HeartRateWorker", "❌ 서버 응답 실패: ${response.code()}")
                    Result.retry()
                }
            } else {
                Log.w("HeartRateWorker", "⚠️ 전송할 데이터가 아직 없음. 1시간 후 재시도 예정")
                delay(RETRY_INTERVAL_MS)
            }
        }

        Log.e("HeartRateWorker", "📛 최대 재시도 횟수 도달. 작업 실패 처리")
        return@withContext Result.failure()
    }

    private fun getLatestHeartRateRequest(email: String): HeartRateUploadRequest? {
        val prefs = context.getSharedPreferences("HeartRateStorage", Context.MODE_PRIVATE)
        val json = prefs.getString("unsentHeartRates", null) ?: return null

        val type = object : com.google.gson.reflect.TypeToken<List<HeartRate>>() {}.type
        val list: List<HeartRate> = Gson().fromJson(json, type)

        val latest = list.maxByOrNull { it.startTime } ?: return null

        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

        return HeartRateUploadRequest(
            email = email,
            startTime = now.minusHours(1).format(formatter),
            endTime = now.format(formatter),
            count = latest.count,
            min = latest.min,
            max = latest.max,
            avg = latest.avg,
            stress = latest.stress
        )
    }

    data class HeartRate(
        var min: Float,
        var max: Float,
        var avg: Float,
        var startTime: String,
        var endTime: String,
        var count: Int,
        var stress: Float = 0f
    )
}
