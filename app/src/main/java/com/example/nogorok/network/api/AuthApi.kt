package com.example.nogorok.network.api

import com.example.nogorok.network.dto.SignInRequest
import com.example.nogorok.network.dto.SignInResponse
import com.example.nogorok.network.dto.SignUpRequest
import com.example.nogorok.network.dto.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/signUp")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): Response<SignUpResponse>

    @POST("/auth/signIn")
    suspend fun signIn(
        @Body request: SignInRequest
    ): Response<SignInResponse>
}
