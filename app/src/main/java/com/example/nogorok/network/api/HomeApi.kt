package com.example.nogorok.network.api

import com.example.nogorok.network.dto.HomeResponse
import com.example.nogorok.network.dto.TourResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import okhttp3.ResponseBody


interface HomeApi {
    @GET("/api/devices/latest")
    suspend fun getLatestStress(): Response<HomeResponse>

    @GET("/api/tour/location")
    suspend fun getTourByLocation(
        @Query("numOfRows") numOfRows: Int = 10,
        @Query("pageNo") pageNo: Int = 1,
        @Query("mobileOS") mobileOS: String = "ETC",
        @Query("mobileApp") mobileApp: String = "AppTest",
        @Query("mapX") mapX: Double,  // 위도
        @Query("mapY") mapY: Double,  // 경도
        @Query("radius") radius: Int = 1000,
        @Query("type") type: String = "json"
    ): Response<TourResponse>

    @GET("/api/crawl/trauma")
    suspend fun getTraumaArticle(): Response<ResponseBody>

    @GET("/api/crawl/samsung-stress")
    suspend fun getSamsungStress(): Response<ResponseBody>

    @GET("/api/crawl/lawtimes-article")
    suspend fun getLawtimesArticle(): Response<ResponseBody>
}