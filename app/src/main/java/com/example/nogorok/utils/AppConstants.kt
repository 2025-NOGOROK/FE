package com.example.nogorok.utils

import java.time.LocalDateTime
import java.time.LocalTime
import kotlinx.coroutines.Dispatchers

object AppConstants {
    const val SUCCESS = "SUCCESS"
    const val WAITING = "WAITING"
    const val NO_PERMISSION = "NO PERMISSION"

    // 현재 사용하는 액티비티 ID: 심박수 관련 화면
    const val HEART_RATE_ACTIVITY = 2

    // 날짜 관련 상수
    val minimumDate: LocalDateTime = LocalDateTime.of(1900, 1, 1, 0, 0)
    val currentDate: LocalDateTime = LocalDateTime.now().with(LocalTime.MIDNIGHT)

    // 디스패처
    val SCOPE_IO_DISPATCHERS = Dispatchers.IO

    // 앱 ID (변경됨)
    const val APP_ID = "com.example.nogorok"
}
