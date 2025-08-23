package com.example.nogorok.network.dto

import com.google.gson.annotations.SerializedName

data class HeartRateSample(
    @SerializedName("timestamp") val timestamp: Long,   // epoch millis
    @SerializedName("heartRate") val heartRate: Int,
    @SerializedName("rmssd") val rmssd: Double,
    @SerializedName("stressEma") val stressEma: Int,    // 0~100 정수
    @SerializedName("stressRaw") val stressRaw: Int     // 0~100 정수
)