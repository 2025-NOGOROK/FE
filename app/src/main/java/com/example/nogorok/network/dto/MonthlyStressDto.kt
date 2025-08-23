package com.example.nogorok.network.dto

data class MonthlyStressDto(
    val year: Int,
    val month: Int,
    val mostStressful: StressfulDay?,
    val leastStressful: StressfulDay?

)

data class StressfulDay(
    val date: String,                  // "2025-08-23"
    val avgStress: Double? = null,     // ← 응답이 100.0 이므로 Double?로
    val shortCount: Int? = null,
    val longCount: Int? = null,
    val emergencyCount: Int? = null,
    val shortEvents: List<CommaEvent>? = emptyList(),
    val longEvents: List<CommaEvent>? = emptyList(),
    val emergencyEvents: List<CommaEvent>? = emptyList()

)

data class CommaEvent(
    val title: String,
    val startTime: String,             // "11:00"
    val endTime: String,               // "12:00"
    val sourceType: String? = null
)
