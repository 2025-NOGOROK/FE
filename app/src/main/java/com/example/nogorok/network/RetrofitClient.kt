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

    private const val BASE_URL = "https://recommend.ai.kr/"

    // 로그 출력용 인터셉터
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Authorization 헤더 자동 추가 인터셉터
    private val authInterceptor = Interceptor { chain ->
        val request = chain.request()
        val url = request.url.encodedPath // 경로만 추출 (ex: /auth/signUp)

        // 인증 제외할 API 경로 (회원가입, 로그인, Google OAuth callback 등)
        val excludedPaths = listOf(
            "/auth/signUp",
            "/auth/signIn",
            "/auth/google/callback"
        )

        val requestBuilder = request.newBuilder()
        val token = TokenManager.getAccessToken()

        // 제외된 경로가 아닐 때만 Authorization 헤더 추가
        if (!excludedPaths.contains(url) && !token.isNullOrEmpty()) {
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
