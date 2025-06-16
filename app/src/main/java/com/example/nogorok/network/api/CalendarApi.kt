package com.example.nogorok.network.api

import com.example.nogorok.network.dto.CalendarResponse
import com.example.nogorok.network.dto.GoogleEventAddRequest
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.POST
import retrofit2.http.Body
import okhttp3.ResponseBody


interface CalendarApi {
    @GET("/auth/google/events")
    suspend fun getGoogleEvents(
        @Header("Authorization") jwt: String,
        @Query("calendarId") calendarId: String = "primary",
        @Query("timeMin") timeMin: String,
        @Query("timeMax") timeMax: String
    ): Response<List<CalendarResponse>>

    @POST("/auth/google/eventsPlus")
    suspend fun addGoogleEvent(
        @Header("Authorization") jwt: String,
        @Body request: GoogleEventAddRequest
    ): Response<ResponseBody>
}