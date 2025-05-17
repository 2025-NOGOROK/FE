package com.example.nogorok.network.dto

data class HeartRateUploadRequest(
    val timestamp: String,
    val min: Float,
    val max: Float,
    val avg: Float,
    val stress: Float
)