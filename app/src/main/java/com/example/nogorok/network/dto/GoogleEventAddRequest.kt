package com.example.nogorok.network.dto

data class GoogleEventAddRequest(
    val calendarId: String = "primary",
    val title: String,
    val description: String,
    val startDateTime: String,
    val endDateTime: String,
    val serverAlarm: Boolean,
    val minutesBeforeAlarm: Int,
    val fixed: Boolean = false,
    val userLabel: Boolean = true
)