package com.example.nogorok.features.schedule

data class ScheduleItem(
    val title: String,
    val time: String,
    val isPinned: Boolean,
    val isShortRest: Boolean = false
)
