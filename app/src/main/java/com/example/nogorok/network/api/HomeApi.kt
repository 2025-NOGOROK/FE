package com.example.nogorok.network.api

import com.example.nogorok.network.dto.HomeResponse
import retrofit2.Response
import retrofit2.http.GET

interface HomeApi {
    @GET("/api/devices/latest")
    suspend fun getLatestStress(): Response<HomeResponse>
}