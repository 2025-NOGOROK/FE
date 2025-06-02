// WeeklyResponse.kt
package com.example.nogorok.network.dto

data class WeeklyResponse(
    val emotionFatigue: List<EmotionFatigueEntry>
)

data class EmotionFatigueEntry(
    val day: String,
    val emotion: String,
    val fatigue: Int
)
