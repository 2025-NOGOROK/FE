package com.example.nogorok.network.dto

// 기존 Emotion + Fatigue 응답
data class WeeklyResponse(
    val days: List<EmotionFatigueEntry>
)

data class EmotionFatigueEntry(
    val dayOfWeek: String,
    val emotion: String,
    val fatigue: String
)

// 스트레스 응답
data class WeeklyStressResponse(
    val days: List<StressEntry>
)

data class StressEntry(
    val dayOfWeek: String,
    val averageStress: Int
)

// 🔹 날씨 응답 추가
data class WeeklyWeatherResponse(
    val days: List<WeatherEntry>
)

data class WeatherEntry(
    val dayOfWeek: String,
    val weather: String
)
