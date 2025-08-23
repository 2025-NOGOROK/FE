package com.example.nogorok.network.dto

data class ApiError(
    val error: String? = null,
    val message: String? = null,
    val authUrl: String? = null
)