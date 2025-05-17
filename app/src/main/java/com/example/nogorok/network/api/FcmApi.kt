package com.example.nogorok.network.api

import com.example.nogorok.network.dto.FcmTokenRequest
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Body


interface FcmApi {
    @POST("/api/fcm/register")
    suspend fun sendFcm(
        @Body request: FcmTokenRequest
    ): Response<Void>

}
