package com.example.nogorok.features.schedule

import java.util.Date

data class Schedule(
    val title: String,         // 일정 제목
    val description: String,   // 일정 설명
    val startDate: Date,       // 일정 시작 시간
    val endDate: Date,         // 일정 종료 시간
    val alarmOption: String,   // 알림 옵션
    val moveAlarm: Boolean,     // 이동 알림 여부
    val type: String = "fixed"
)
