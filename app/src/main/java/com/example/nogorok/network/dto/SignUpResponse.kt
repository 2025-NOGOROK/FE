package com.example.nogorok.network.dto

data class SignUpResponse(
    val message: String,
    val data: TokenData
)

data class TokenData(
    val accessToken: String,
    val refreshToken: String,
    val message: String
)
