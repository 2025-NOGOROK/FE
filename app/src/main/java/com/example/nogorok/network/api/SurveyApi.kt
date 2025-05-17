package com.example.nogorok.network.api

import com.example.nogorok.network.dto.SurveyRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SurveyApi {
    @POST("/api/survey")
    suspend fun submitSurvey(@Body request: SurveyRequest): Response<Void>
}
