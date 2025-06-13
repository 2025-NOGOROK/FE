package com.example.nogorok.network.dto

data class GoogleTokenResponse(
    val access_token: String,
    val refresh_token: String,
    val expires_in: Int,
    val token_type: String,
    val scope: String,
    val id_token: String? = null
)
