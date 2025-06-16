package com.example.nogorok.network.dto

data class LongRestResponse(
    val type: String,
    val label: String,
    val data: List<LongRestEventItem>,
    val startTime: String?,
    val endTime: String?
)

data class LongRestEventItem(
    val title: String?,
    val description: String?,
    val startTime: String?,
    val endTime: String?,
    val startDateTime: String?,
    val endDateTime: String?
)
