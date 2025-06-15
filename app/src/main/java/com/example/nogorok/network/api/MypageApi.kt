package com.example.nogorok.network.api

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.GET
import okhttp3.ResponseBody

interface MypageApi {
    @DELETE("/api/mypage/user/delete")
    suspend fun deleteUser(): Response<ResponseBody>

    @GET("/api/mypage/name")
    suspend fun getUserName(
    ): Response<ResponseBody>
}
