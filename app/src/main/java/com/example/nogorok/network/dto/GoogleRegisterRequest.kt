package com.example.nogorok.network.dto

data class GoogleRegisterRequest(
    val access_token: String,
    val refresh_token: String
)