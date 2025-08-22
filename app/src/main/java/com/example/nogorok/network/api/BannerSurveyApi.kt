package com.example.nogorok.network.api

import com.example.nogorok.network.dto.BannerSurveyRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BannerSurveyApi {
    @POST("/api/survey/banner")
    suspend fun submitBannerSurvey(
        @Body request: BannerSurveyRequest
    ): Response<Void>
}
