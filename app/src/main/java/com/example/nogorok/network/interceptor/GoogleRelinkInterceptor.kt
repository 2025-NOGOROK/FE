package com.example.nogorok.network.interceptor

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.nogorok.network.util.asApiError
import okhttp3.Interceptor
import okhttp3.Response

class GoogleRelinkInterceptor(
    private val appContext: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val res = chain.proceed(chain.request())
        if (res.code == 401) {
            val copy = res.peekBody(Long.MAX_VALUE)
            val err = copy.asApiError()
            if (err?.error == "GOOGLE_RELINK_REQUIRED" && !err.authUrl.isNullOrBlank()) {
                // 브라우저에서 Google 동의 화면 오픈 (앱 컨텍스트 사용 → NEW_TASK 필수)
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(err.authUrl))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                appContext.startActivity(i)
            }
        }
        return res
    }
}
