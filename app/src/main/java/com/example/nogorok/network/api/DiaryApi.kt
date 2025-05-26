package com.example.nogorok.network.api

import com.example.nogorok.network.dto.DiaryRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DiaryApi {
    @POST("/api/haru")
    suspend fun postDiary(@Body request: DiaryRequest): Response<Void>
}