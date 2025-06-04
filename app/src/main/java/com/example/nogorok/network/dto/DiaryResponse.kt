package com.example.nogorok.network.dto

data class DiaryResponse(
    val emotion: String,
    val fatigue: String,
    val weather: String,
    val sleepHours: Int,
    val specialNotes: String
)