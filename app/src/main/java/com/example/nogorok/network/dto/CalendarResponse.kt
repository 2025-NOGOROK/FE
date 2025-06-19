package com.example.nogorok.network.dto

data class CalendarResponse(
    val title: String,
    val description: String,
    val startDateTime: String,
    val endDateTime: String,
    val sourceType: String,
)