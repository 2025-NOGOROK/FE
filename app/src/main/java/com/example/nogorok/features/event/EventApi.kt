package com.example.nogorok.features.event


import retrofit2.http.GET
import retrofit2.http.Query

interface EventApi {

    // ✅ 위치 기반 행사 리스트 조회
    @GET("/api/tour/location")
    suspend fun getEventList(
        @Query("numOfRows") numOfRows: Int = 10,
        @Query("pageNo") pageNo: Int = 1,
        @Query("mapX") mapX: Double,   // 경도
        @Query("mapY") mapY: Double    // 위도
    ): EventListResponse

    // ✅ 특정 행사 상세 조회
    @GET("/api/tour/detail")
    suspend fun getEventDetail(
        @Query("seq") seq: Int
    ): EventDetailResponse
}
