package com.example.nogorok.network.api

import com.example.nogorok.network.dto.JwtResponse
import com.example.nogorok.network.dto.GoogleTokenResponse
import com.example.nogorok.network.dto.GoogleRegisterRequest
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.GET
import retrofit2.http.Body

interface GoogleApi {
    @GET("/auth/google/token")
    suspend fun getGoogleTokens(
        @Query("code") code: String
    ): Response<GoogleTokenResponse>

    @POST("/auth/google/mobile-register")
    suspend fun registerGoogleToken(
        @Body body: GoogleRegisterRequest
    ): Response<JwtResponse>
}