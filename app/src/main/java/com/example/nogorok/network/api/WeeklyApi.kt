package com.example.nogorok.network.api

import com.example.nogorok.network.dto.WeeklyResponse
import com.example.nogorok.network.dto.WeeklyStressResponse
import com.example.nogorok.network.dto.WeeklyWeatherResponse
import retrofit2.http.GET

interface WeeklyApi {
    @GET("/api/weekly/emotion-fatigue")
    suspend fun getWeeklyEmotionFatigue(): WeeklyResponse

    @GET("/api/weekly/stress")
    suspend fun getWeeklyStress(): WeeklyStressResponse

    @GET("/api/weekly/weather")  // 🔹 날씨 API 추가
    suspend fun getWeeklyWeather(): WeeklyWeatherResponse
}
