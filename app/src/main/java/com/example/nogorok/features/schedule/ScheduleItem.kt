package com.example.nogorok.features.schedule

data class ScheduleItem(
    val title: String,
    val startTime: String,  // 예: "10:00"
    val endTime: String,    // 예: "11:00"
    val isPinned: Boolean,
    val isShortRest: Boolean = false
)
