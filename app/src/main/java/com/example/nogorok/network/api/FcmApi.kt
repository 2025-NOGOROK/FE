package com.example.nogorok.network.api

import com.example.nogorok.network.dto.FcmScheduleRequest
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Header
import retrofit2.http.Body
import okhttp3.ResponseBody
import retrofit2.Response

interface FcmApi {
    @POST("/api/fcm/register")
    suspend fun registerFcmToken(
        @Query("token") token: String
    )

    @POST("/api/fcm/schedule")
    suspend fun registerFcmSchedule(
        @Body request: FcmScheduleRequest
    ): Response<ResponseBody>

}
