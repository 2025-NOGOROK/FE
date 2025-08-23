package com.example.nogorok.network.interceptor

import com.example.nogorok.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthHeaderInterceptor(
    private val getToken: () -> String?
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = getToken()
        val req = if (!token.isNullOrBlank()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else chain.request()
        return chain.proceed(req)
    }
}
