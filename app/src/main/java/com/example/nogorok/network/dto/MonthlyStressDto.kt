package com.example.nogorok.network.dto

// 월간 스트레스 & 쉼표 리포트 응답
data class MonthlyStressDto(
    val year: Int,
    val month: Int,
    val mostStressful: StressfulDay
)

// 가장 스트레스가 높았던 하루의 상세
data class StressfulDay(
    val date: String,          // 예: "2025-08-23"
    val avgStress: Int,        // 그 날 평균 스트레스 지수
    val shortCount: Int,       // 그 날 '짧은 쉼표' 개수
    val longCount: Int,        // 그 날 '긴 쉼표' 개수
    val emergencyCount: Int,   // 그 날 '긴급 쉼표' 개수
    val shortEvents: List<ReportEvent>,
    val longEvents: List<ReportEvent>
)

// 쉼표 이벤트 공통 모델
data class ReportEvent(
    val title: String,
    val startTime: String,
    val endTime: String,
    val sourceType: String
)