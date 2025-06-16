package com.example.nogorok.network.dto

data class FcmScheduleRequest(
    val title: String,
    val startDateTime: String, // ISO 8601 (예: 2025-06-01T14:00:00+09:00)
    val minutesBeforeAlarm: Int // 1~1440
)