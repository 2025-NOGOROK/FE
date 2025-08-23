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

object RetrofitClient {

    private const val BASE_URL = "https://recommend.ai.kr"

    @Volatile private var accessToken: String? = null
    fun setAccessToken(token: String?) { accessToken = token }

    @Volatile private var tokenProvider: (() -> String?)? = null
    fun setTokenProvider(provider: () -> String?) { tokenProvider = provider }

    // 🔧 init 추가
    fun init(context: Context) {
        Log.d("RetrofitClient", "RetrofitClient initialized with context=$context")
        // 지금은 특별히 context로 할 건 없지만,
        // SharedPreferences, 캐시 디렉토리 접근 등 필요시 여기에 추가
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
        redactHeader("Authorization")
    }

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request()
        val urlPath = request.url.encodedPath

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
    val eventApi: EventApi by lazy { retrofit.create(EventApi::class.java) }
    val stressApi: StressApi by lazy { retrofit.create(StressApi::class.java) }
}
