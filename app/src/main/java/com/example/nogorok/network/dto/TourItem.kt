package com.example.nogorok.network.dto

data class TourItem(
    val seq: Int,             // 고유 ID
    val title: String?,       // 행사명
    val area: String?,        // 지역 (ex: 서울)
    val sigungu: String?,     // 시군구 (ex: 종로구)
    val place: String?,       // 장소명
    val placeAddr: String?,   // 장소 주소
    val realmName: String?,   // 카테고리 (연극/전시 등)
    val thumbnail: String?,   // 대표 이미지 URL
    val startDate: String?,   // 시작일
    val endDate: String?      // 종료일
)
