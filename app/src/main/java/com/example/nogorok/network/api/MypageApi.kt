package com.example.nogorok.network.api

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Header

interface MypageApi {

    @POST("/api/mypage/logout")
    suspend fun logout(
        @Header("Authorization") accessToken: String
    ): Response<String>
}
