package com.example.nogorok.network.dto

data class SignInResponse(
    val message: String,
    val data: SignInData
)

data class SignInData(
    val accessToken: String,
    val refreshToken: String,
    val message: String
)
