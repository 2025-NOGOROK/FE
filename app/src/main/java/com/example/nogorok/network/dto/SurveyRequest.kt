package com.example.nogorok.network.dto

data class SurveyRequest(
    val scheduleType: String,
    val suddenChangePreferred: Boolean,
    val chronotype: String,
    val preferAlone: String,
    val stressReaction: String,
    val hasStressRelief: Boolean,
    val stressReliefMethods: List<String>
)
