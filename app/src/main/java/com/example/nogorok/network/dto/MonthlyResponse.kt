package com.example.nogorok.network.dto

data class MonthlyResponse(
    val emotionPercent: Map<String, Double>
)

// 🔹 월간 스트레스 응답 추가
data class MonthlyStressResponse(
    val dailyStressList: List<DailyStressEntry>
)

data class DailyStressEntry(
    val date: String,     // ex: "2025-05-01"
    val avg: Double,      // 평균 스트레스
    val emoji: String     // "20", "40", "60", ...
)
