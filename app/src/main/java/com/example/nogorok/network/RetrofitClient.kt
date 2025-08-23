package com.example.nogorok.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import com.example.nogorok.network.api.*

object RetrofitClient {

    private const val BASE_URL = "https://recommend.ai.kr"

    // 수동 주입도 허용(백업용). 가능하면 tokenProvider를 쓰는 걸 추천
    @Volatile private var accessToken: String? = null
    fun setAccessToken(token: String?) { accessToken = token }

    // ★ 매 요청 시 호출되는 토큰 공급자(lambda)
    @Volatile private var tokenProvider: (() -> String?)? = null
    fun setTokenProvider(provider: () -> String?) { tokenProvider = provider }

    // 로그 인터셉터(Authorization 헤더는 마스킹)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
        redactHeader("Authorization")
    }

    // Authorization 헤더 자동 추가
    private val authInterceptor = Interceptor { chain ->
        val request = chain.request()
        val urlPath = request.url.encodedPath

        // 로그인/회원가입 등 토큰 불필요 엔드포인트만 예외 처리
        val excludedPaths = setOf(
            "/auth/signUp",
            "/auth/signIn",
            "/auth/google/callback",
            "/auth/google/mobile-register",
        )

        val rb = request.newBuilder()

        // ★ provider 우선 → 없으면 accessToken 백업 사용
        val token = try { tokenProvider?.invoke() } catch (e: Exception) {
            Log.w("RetrofitClient", "tokenProvider error: ${e.message}")
            null
        } ?: accessToken

        if (!excludedPaths.contains(urlPath) && !token.isNullOrBlank()) {
            rb.addHeader("Authorization", "Bearer $token")
        }

        chain.proceed(rb.build())
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // === APIs ===
    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val surveyApi: SurveyApi by lazy { retrofit.create(SurveyApi::class.java) }
    val healthApi: HealthApi by lazy { retrofit.create(HealthApi::class.java) }
    val fcmApi: FcmApi by lazy { retrofit.create(FcmApi::class.java) }
    val googleApi: GoogleApi by lazy { retrofit.create(GoogleApi::class.java) }
    val diaryApi: DiaryApi by lazy { retrofit.create(DiaryApi::class.java) }
    val mypageApi: MypageApi by lazy { retrofit.create(MypageApi::class.java) }
    val monthlyApi: MonthlyApi by lazy { retrofit.create(MonthlyApi::class.java) }
    val weeklyApi: WeeklyApi by lazy { retrofit.create(WeeklyApi::class.java) }
    val homeApi: HomeApi by lazy { retrofit.create(HomeApi::class.java) }
    val shortRestApi: ShortRestApi by lazy { retrofit.create(ShortRestApi::class.java) }
    val calendarApi: CalendarApi by lazy { retrofit.create(CalendarApi::class.java) }
    val longrestApi: LongRestApi by lazy { retrofit.create(LongRestApi::class.java) }
    val bannerSurveyApi: BannerSurveyApi by lazy { retrofit.create(BannerSurveyApi::class.java) }
    val deviceApi: DeviceApi by lazy { retrofit.create(DeviceApi::class.java) }
}
