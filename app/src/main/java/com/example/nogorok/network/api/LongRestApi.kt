package com.example.nogorok.network.api

import com.example.nogorok.network.dto.LongRestResponse
import com.example.nogorok.network.dto.ChoiceRequest
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Query
import okhttp3.ResponseBody


interface LongRestApi {
    @GET("/api/long-recommend")
    suspend fun getLongRestRecommendations(): Response<List<LongRestResponse>>

    @POST("/api/long-recommend/choice")
    suspend fun postScenarioChoice(
        @Body request: ChoiceRequest
    ): Response<ResponseBody>  // 또는 Response<Unit>

    @GET("/api/long-recommend/tomorrow")
    suspend fun getLongRestRecommendationsWithLocation(@Query("latitude") lat: Double, @Query("longitude") lng: Double): Response<List<LongRestResponse>>
}
