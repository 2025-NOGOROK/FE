package com.example.nogorok.network.dto

data class MonthlyStressTrendDto(
    val points: List<StressPoint> = emptyList()
)

data class StressPoint(
    val year: Int,
    val month: Int,
    val value: Float   // 서버가 Int여도 Float로 받아도 무방(그려야 하니까)
)
