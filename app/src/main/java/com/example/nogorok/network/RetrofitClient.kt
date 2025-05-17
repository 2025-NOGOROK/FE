package com.example.nogorok.network

import com.example.nogorok.network.api.AuthApi
import com.example.nogorok.network.api.SurveyApi
import com.example.nogorok.network.api.HealthApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 실제 백엔드 API 주소
    private const val BASE_URL = "http://3.35.81.229:8080/"

    // 로깅 인터셉터 설정
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // 요청 및 응답 body 모두 로그에 출력
    }

    // OkHttpClient에 인터셉터 추가
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // Retrofit 생성
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // ← 여기에 OkHttpClient 주입
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
}
