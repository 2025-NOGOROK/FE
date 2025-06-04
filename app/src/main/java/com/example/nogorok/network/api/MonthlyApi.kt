package com.example.nogorok.network.api

import com.example.nogorok.network.dto.MonthlyResponse
import com.example.nogorok.network.dto.MonthlyStressResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MonthlyApi {
    @GET("/api/monthly/emotion")
    suspend fun getMonthlyEmotion(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): MonthlyResponse

    // 🔹 월간 스트레스 API 추가
    @GET("/api/monthly/stress")
    suspend fun getMonthlyStress(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): MonthlyStressResponse
}
