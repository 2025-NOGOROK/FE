package com.example.nogorok.network.dto

data class ShortRestResponse(
    val title: String,
    val description: String,
    val startTime: String,
    val endTime: String,
    val sourceType: String,
    val startDateTime: String,
    val endDateTime: String
)