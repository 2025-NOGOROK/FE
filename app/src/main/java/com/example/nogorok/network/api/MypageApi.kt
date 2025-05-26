package com.example.nogorok.network.api

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Header

interface MypageApi {
    @DELETE("/api/mypage/user/delete")
    suspend fun deleteUser(): Response<okhttp3.ResponseBody>
}
