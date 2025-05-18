package com.example.nogorok.network.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

data class TokenResponse(
    val access_token: String,
    val refresh_token: String,
    val expires_in: Long,
    val token_type: String,
    val email: String
)

interface GoogleApi {
    @GET("/auth/google/callback")
    suspend fun getToken(
        @Query("code") code: String
    ): Response<TokenResponse>
}