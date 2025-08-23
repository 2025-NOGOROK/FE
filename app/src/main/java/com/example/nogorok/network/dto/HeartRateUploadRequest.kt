package com.example.nogorok.network.dto

import com.google.gson.annotations.SerializedName

data class HeartRateUploadRequest(
    @SerializedName("email") val email: String,
    @SerializedName("samples") val samples: List<HeartRateSample>
)