package com.example.nogorok.network.dto

data class ResetPasswordRequest(
    val email: String,
    val newPassword: String,
    val confirmPassword: String
)