package com.example.nogorok.network.api

import com.example.nogorok.network.dto.ShortRestResponse
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ShortRestApi {
    @POST("/api/short-recommend")
    suspend fun getShortRest(@Query("date") date: String): List<ShortRestResponse>
}
