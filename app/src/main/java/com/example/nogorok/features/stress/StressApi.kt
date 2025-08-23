// 경로: com.example.nogorok.features.stress.StressApi.kt
package com.example.nogorok.features.stress

import retrofit2.http.GET

interface StressApi {
    @GET("/api/mainpage/crawlSamsungStressPage")
    suspend fun getStressPage(): StressResponse
}
