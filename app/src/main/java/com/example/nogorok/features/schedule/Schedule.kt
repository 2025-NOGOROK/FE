package com.example.nogorok.features.schedule

import java.util.Date

data class Schedule(
    val title: String,
    val description: String,
    val startDate: Date,
    val endDate: Date,
    val alarmOption: String,
    val moveAlarm: Boolean
)
