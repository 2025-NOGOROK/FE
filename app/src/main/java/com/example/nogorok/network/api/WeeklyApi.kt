// WeeklyApi.kt
package com.example.nogorok.network.api

import com.example.nogorok.network.dto.WeeklyResponse
import retrofit2.http.GET

interface WeeklyApi {
    @GET("/api/weekly/emotion-fatigue")
    suspend fun getWeeklyEmotionFatigue(): WeeklyResponse
}
