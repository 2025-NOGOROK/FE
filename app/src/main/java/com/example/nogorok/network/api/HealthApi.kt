// HealthApi.kt
package com.example.nogorok.network.api

import com.example.nogorok.network.dto.HeartRateUploadRequest
import com.example.nogorok.network.dto.SleepUploadRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface HealthApi {
    @POST("api/devices/heartrate")
    suspend fun uploadHeartRate(@Body body: HeartRateUploadRequest): Response<Void>

    @POST("api/health/sleep")
    suspend fun uploadSleep(@Body body: SleepUploadRequest): Response<Void>
}
