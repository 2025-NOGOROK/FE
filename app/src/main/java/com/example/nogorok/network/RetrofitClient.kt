package com.example.nogorok.network

import com.example.nogorok.network.api.AuthApi
import com.example.nogorok.network.api.FcmApi
import com.example.nogorok.network.api.HealthApi
import com.example.nogorok.network.api.SurveyApi
import com.example.nogorok.network.api.GoogleApi
import com.example.nogorok.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://3.35.81.229:8080/"

    // 로그 출력용 인터셉터
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Authorization 헤더 자동 추가 인터셉터
    private val authInterceptor = Interceptor { chain ->
        val token = TokenManager.getAccessToken()
        val requestBuilder = chain.request().newBuilder()
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        chain.proceed(requestBuilder.build())
    }

    // OkHttpClient 설정
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)        // accessToken 자동 추가
        .addInterceptor(loggingInterceptor)     // 로그 출력
        .build()

    // Retrofit 인스턴스
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val surveyApi: SurveyApi by lazy {
        retrofit.create(SurveyApi::class.java)
    }

    val healthApi: HealthApi by lazy {
        retrofit.create(HealthApi::class.java)
    }

    val fcmApi: FcmApi by lazy {
        retrofit.create(FcmApi::class.java)
    }

    val googleApi: GoogleApi by lazy {
        retrofit.create(GoogleApi::class.java)
    }

}
