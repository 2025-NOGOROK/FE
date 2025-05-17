package com.example.nogorok.network.api

import com.example.nogorok.network.dto.SignUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/signUp")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): Response<Void>
}
