package com.example.nogorok.network.dto

data class MonthlyCountsDto(
    val year: Int,
    val month: Int,
    val shortCount: Int,         // 이달 '짧은 쉼표' 총 개수
    val longCount: Int,          // 이달 '긴 쉼표' 총 개수
    val shortDiffFromPrev: Int,  // 전월 대비 증감(+) 개수
    val longDiffFromPrev: Int    // 전월 대비 증감(+) 개수
)
