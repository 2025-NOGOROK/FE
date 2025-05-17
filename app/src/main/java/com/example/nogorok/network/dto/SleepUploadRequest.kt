package com.example.nogorok.network.dto

data class SleepUploadRequest(
    val startTime: String,
    val endTime: String,
    val totalMinutes: Int,
    val bloodOxygen: Float?,
    val skinTemperature: Float?
)