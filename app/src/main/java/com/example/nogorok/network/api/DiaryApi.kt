package com.example.nogorok.network.api

import com.example.nogorok.network.dto.DiaryRequest
import com.example.nogorok.network.dto.DiaryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query

interface DiaryApi {
    @POST("/api/haru")
    suspend fun postDiary(@Body request: DiaryRequest): Response<Void>

    @GET("/api/haru")
    suspend fun getDiary(@Query("date") date: String): Response<DiaryResponse>

}