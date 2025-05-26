package com.example.nogorok.network.dto

data class DiaryRequest(
    val date: String,
    val emotion: String,
    val fatigueLevel: String,
    val weather: String,
    val sleepHours: Int = 0,
    val specialNotes: String = ""
)