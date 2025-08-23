package com.example.nogorok.network.api

import com.example.nogorok.network.dto.MonthlyCountsDto
import com.example.nogorok.network.dto.MonthlyStressDto
import com.example.nogorok.network.dto.MonthlyStressTrendDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MonthlyApi {
    @GET("/api/report/monthly")
    suspend fun getMonthly(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<MonthlyCountsDto>

    @GET("/api/report/monthly-stress")
    suspend fun getMonthlyStress(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<MonthlyStressDto>

    @GET("/api/report/monthly-stress-trend")
    suspend fun getMonthlyStressTrend(): Response<MonthlyStressTrendDto>
}
