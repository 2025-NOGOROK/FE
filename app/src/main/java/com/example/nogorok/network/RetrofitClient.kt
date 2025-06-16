package com.example.nogorok.network

import com.example.nogorok.network.api.AuthApi
import com.example.nogorok.network.api.FcmApi
import com.example.nogorok.network.api.HealthApi
import com.example.nogorok.network.api.SurveyApi
import com.example.nogorok.network.api.GoogleApi
import com.example.nogorok.network.api.DiaryApi
import com.example.nogorok.network.api.MypageApi
import com.example.nogorok.network.api.MonthlyApi
import com.example.nogorok.network.api.WeeklyApi
import com.example.nogorok.network.api.HomeApi
import com.example.nogorok.network.api.ShortRestApi
import com.example.nogorok.network.api.CalendarApi
import com.example.nogorok.network.api.LongRestApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://recommend.ai.kr/"

    // 외부에서 주입하는 토큰 변수 (로그인/연동 후 설정 필요)
    private var accessToken: String? = null

    fun setAccessToken(token: String?) {
        accessToken = token
    }

    // 로그 출력용 인터셉터
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Authorization 헤더 자동 추가 인터셉터
    private val authInterceptor = Interceptor { chain ->
        val request = chain.request()
        val url = request.url.encodedPath

        val excludedPaths = listOf(
            "/auth/signUp",
            "/auth/signIn",
            "/auth/google/callback",
            "/auth/google/mobile-register",
            "/auth/google/events",
            "/auth/google/eventsPlus",
            "/api/short-recommend"// ✅ 구글 연동 시 제외 경로 추가
        )

        val requestBuilder = request.newBuilder()

        if (!excludedPaths.contains(url) && !accessToken.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $accessToken")
        }

        chain.proceed(requestBuilder.build())
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

    // 각 API 인터페이스
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
}
