package com.example.nogorok.features.rest.longrest

data class RestItem(
    val title: String,
    val description: String,
    val startTime: String,
    val endTime: String,
    val sourceType: String,
    val startDateTime: String,
    val endDateTime: String
)