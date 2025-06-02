package com.example.nogorok.network.api

import retrofit2.http.POST
import retrofit2.http.Query

interface FcmApi {
    @POST("/api/fcm/register")
    suspend fun registerFcmToken(
        @Query("token") token: String
    )
}
