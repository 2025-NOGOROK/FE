package com.example.nogorok.network.api

import com.example.nogorok.network.dto.HeartRateUploadRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DeviceApi {
    @POST("/api/devices/heartrate")
    suspend fun uploadHeartRate(
        @Body body: HeartRateUploadRequest
    ): Response<Unit>  // 성공 시 바디 없으면 Unit
}