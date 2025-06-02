package com.example.nogorok.network.api

import com.example.nogorok.network.dto.SurveyRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface SurveyApi {
    @POST("/api/survey")
    suspend fun submitSurvey(@Body request: SurveyRequest): Response<Void>

    @PUT("/api/mypage/survey")
    suspend fun updateSurvey(
        @Body request: SurveyRequest
    ): Response<Void>
}
