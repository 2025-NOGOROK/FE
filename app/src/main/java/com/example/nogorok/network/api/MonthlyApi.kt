package com.example.nogorok.network.api

import com.example.nogorok.network.dto.MonthlyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MonthlyApi {
    @GET("/api/monthly/emotion")
    suspend fun getMonthlyEmotion(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): MonthlyResponse
}
