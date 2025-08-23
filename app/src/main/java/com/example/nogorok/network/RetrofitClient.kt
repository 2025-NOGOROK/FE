package com.example.nogorok.network

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.nogorok.network.api.*
import com.example.nogorok.features.event.EventApi
import com.example.nogorok.features.stress.StressApi
import com.example.nogorok.network.interceptor.GoogleRelinkInterceptor

object RetrofitClient {

    private const val BASE_URL = "https://recommend.ai.kr/" // ← 반드시 슬래시로 끝나야 함

    // 수동 주입 백업용
    @Volatile private var accessToken: String? = null
    fun setAccessToken(token: String?) { accessToken = token }

    // 매 요청 시 토큰 공급자
    @Volatile private var tokenProvider: (() -> String?)? = null
    fun setTokenProvider(provider: () -> String?) { tokenProvider = provider }

    // 로그 (Authorization 헤더 마스킹)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
        redactHeader("Authorization")
    }

    // Authorization 헤더 자동 추가
    private val authInterceptor = Interceptor { chain ->
        val request = chain.request()
        val urlPath = request.url.encodedPath

        // 토큰 불필요 엔드포인트
        val excludedPaths = setOf(
            "/auth/signUp",
            "/auth/signIn",
            "/auth/google/callback",
            "/auth/google/mobile-register",
        )

        val rb = request.newBuilder()

        val token = try { tokenProvider?.invoke() } catch (e: Exception) {
            Log.w("RetrofitClient", "tokenProvider error: ${e.message}")
            null
        } ?: accessToken

        if (!excludedPaths.contains(urlPath) && !token.isNullOrBlank()) {
            rb.addHeader("Authorization", "Bearer $token")
        }

        chain.proceed(rb.build())
    }

    // ---- Retrofit/OkHttp 인스턴스들 ----
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var retrofit: Retrofit

    // === 공개 API 서비스 ===
    lateinit var authApi: AuthApi
        private set
    lateinit var surveyApi: SurveyApi
        private set
    lateinit var healthApi: HealthApi
        private set
    lateinit var fcmApi: FcmApi
        private set
    lateinit var googleApi: GoogleApi
        private set
    lateinit var diaryApi: DiaryApi
        private set
    lateinit var mypageApi: MypageApi
        private set
    lateinit var monthlyApi: MonthlyApi
        private set
    lateinit var weeklyApi: WeeklyApi
        private set
    lateinit var homeApi: HomeApi
        private set
    lateinit var shortRestApi: ShortRestApi
        private set
    lateinit var calendarApi: CalendarApi
        private set
    lateinit var longrestApi: LongRestApi
        private set
    lateinit var bannerSurveyApi: BannerSurveyApi
        private set
    lateinit var deviceApi: DeviceApi
        private set
    lateinit var eventApi: EventApi
        private set
    lateinit var stressApi: StressApi
        private set

    // ✨ 앱 시작 시 Application에서 한 번 호출
    fun init(appContext: Context) {
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(GoogleRelinkInterceptor(appContext)) // 401 재연동 처리
            .addInterceptor(loggingInterceptor)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // 서비스 구현체 생성
        authApi         = retrofit.create(AuthApi::class.java)
        surveyApi       = retrofit.create(SurveyApi::class.java)
        healthApi       = retrofit.create(HealthApi::class.java)
        fcmApi          = retrofit.create(FcmApi::class.java)
        googleApi       = retrofit.create(GoogleApi::class.java)
        diaryApi        = retrofit.create(DiaryApi::class.java)
        mypageApi       = retrofit.create(MypageApi::class.java)
        monthlyApi      = retrofit.create(MonthlyApi::class.java)
        weeklyApi       = retrofit.create(WeeklyApi::class.java)
        homeApi         = retrofit.create(HomeApi::class.java)
        shortRestApi    = retrofit.create(ShortRestApi::class.java)
        calendarApi     = retrofit.create(CalendarApi::class.java)
        longrestApi     = retrofit.create(LongRestApi::class.java)
        bannerSurveyApi = retrofit.create(BannerSurveyApi::class.java)
        deviceApi       = retrofit.create(DeviceApi::class.java)
        eventApi        = retrofit.create(EventApi::class.java)
        stressApi       = retrofit.create(StressApi::class.java)
    }
}
