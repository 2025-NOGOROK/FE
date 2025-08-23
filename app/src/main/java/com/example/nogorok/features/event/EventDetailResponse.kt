package com.example.nogorok.features.event

data class EventDetailResponse(
    val response: DetailResponseBody
)

data class DetailResponseBody(
    val body: DetailBody
)

data class DetailBody(
    val items: DetailItems
)

data class DetailItems(
    val item: EventDetailItem
)

data class EventDetailItem(
    val seq: Int,
    val title: String?,
    val realmName: String?,
    val startDate: String?,
    val endDate: String?,
    val place: String?,
    val placeAddr: String?,
    val imgUrl: String?,
    val contents1: String?,
    val gpsX: Double?,   // ✅ 경도
    val gpsY: Double?,   // ✅ 위도
    val url: String?     // ✅ 상세 URL 추가
)
