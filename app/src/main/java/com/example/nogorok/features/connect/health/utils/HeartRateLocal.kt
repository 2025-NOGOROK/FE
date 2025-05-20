package com.example.nogorok.features.connect.health.utils

data class HeartRateLocal(
    val min: Float,
    val max: Float,
    val avg: Float,
    val startTime: String,
    val endTime: String,
    val count: Int,
    val stress: Float = 0f
)
