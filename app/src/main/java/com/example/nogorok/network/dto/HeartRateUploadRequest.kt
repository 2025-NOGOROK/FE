package com.example.nogorok.network.dto

data class HeartRateUploadRequest(
    val email: String,
    var startTime: String,
    var endTime: String,
    var count: Int,
    val min: Float,
    val max: Float,
    val avg: Float,
    val stress: Float
)