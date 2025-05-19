// Schedule.kt
package com.example.nogorok.features.schedule

data class Schedule(
    val id: Long = System.currentTimeMillis(), // 고유 id, 수정시 구분용
    val title: String,
    val description: String,
    val startDate: String,
    val startTime: String,
    val endDate: String,
    val endTime: String
)
