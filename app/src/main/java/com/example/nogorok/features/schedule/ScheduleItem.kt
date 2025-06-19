package com.example.nogorok.features.schedule

data class ScheduleItem(
    val title: String,
    val startTime: String,
    val endTime: String,
    val isPinned: Boolean,
    val sourceType: String
)
