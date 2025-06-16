package com.example.nogorok.network.dto

data class ChoiceRequest(
    val type: String,
    val label: String,
    val data: List<LongRestEventItem>,
    val startTime: String,
    val endTime: String
)
