package com.example.nogorok.network.dto

data class TourResponse(
    val response: TourResponseBody
)

data class TourResponseBody(
    val body: TourBody
)

data class TourBody(
    val items: TourItems
)

data class TourItems(
    val item: List<TourItem>   // ✅ 여기서 TourItem은 network/dto/TourItem.kt 의 클래스
)
