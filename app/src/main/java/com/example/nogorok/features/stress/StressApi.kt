// 경로: com.example.nogorok.features.stress.StressApi.kt
package com.example.nogorok.features.stress

import retrofit2.http.GET
import retrofit2.http.Query

interface StressApi {

    // ✅ linkUrl 기준으로 크롤링된 페이지 내용 가져오기
    @GET("/api/crawl/teen")
    suspend fun getStressPage(
        @Query("linkUrl") linkUrl: String
    ): List<StressContentResponse>
}
