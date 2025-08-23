package com.example.nogorok.features.event

data class EventListResponse(
    val response: EventListResponseBody
)

data class EventListResponseBody(
    val body: EventListBody
)

data class EventListBody(
    val items: EventListItems
)

data class EventListItems(
    val item: List<EventListItem>
)

/**
 * ✅ 행사/전시 목록 아이템
 */
data class EventListItem(
    val seq: Int,             // 고유 ID
    val title: String?,       // 행사명
    val area: String?,        // 지역 (서울 등)
    val sigungu: String?,     // 시군구
    val place: String?,       // 장소명
    val placeAddr: String?,   // 주소
    val realmName: String?,   // 카테고리 (연극/전시 등)
    val thumbnail: String?,   // 대표 이미지 URL
    val startDate: String?,   // 시작일
    val endDate: String?,     // 종료일
    val gpsX: Double?,        // 경도
    val gpsY: Double?         // 위도
)
