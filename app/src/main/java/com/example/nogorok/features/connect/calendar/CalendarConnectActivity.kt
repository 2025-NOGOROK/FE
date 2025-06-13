package com.example.nogorok.features.connect.calendar

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nogorok.databinding.ActivityCalendarConnectBinding
import com.example.nogorok.features.connect.ConnectActivity
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.GoogleRegisterRequest
import com.example.nogorok.network.dto.GoogleTokenResponse
import com.example.nogorok.network.dto.JwtResponse
import com.example.nogorok.utils.TokenManager
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class CalendarConnectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarConnectBinding

    // Google OAuth 정보 (실제 값으로 교체 필요)
    private val clientId = "1062370068118-f2cgskgioh5eeb14rqq5di0tsn30c1et.apps.googleusercontent.com"
    private val clientSecret = "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"
    private val redirectUri = "https://recommend.ai.kr/auth/google/callback"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ⬅️ 뒤로가기 버튼
        binding.btnBack.setOnClickListener {
            finish()
        }

        // ✅ "네, 연동할래요" 버튼 → Google OAuth 로그인 진입
        binding.btnConnect.setOnClickListener {
            val scopeList = listOf(
                "https://www.googleapis.com/auth/calendar",
                "https://www.googleapis.com/auth/calendar.events",
                "https://www.googleapis.com/auth/calendar.freebusy",
                "https://www.googleapis.com/auth/userinfo.email",
                "https://www.googleapis.com/auth/userinfo.profile"
            )

            val authUri = Uri.parse(
                "https://accounts.google.com/o/oauth2/v2/auth?" +
                        "client_id=$clientId&" +
                        "redirect_uri=$redirectUri&" +
                        "response_type=code&" +
                        "scope=${scopeList.joinToString("%20")}&" +
                        "access_type=offline&" +
                        "prompt=consent"
            )

            startActivity(Intent(Intent.ACTION_VIEW, authUri))
        }

        // ❌ "아니요" → 이전 화면
        binding.btnNew.setOnClickListener {
            startActivity(Intent(this, ConnectActivity::class.java))
            finish()
        }

        // ✅ 앱이 딥링크로 실행되었을 경우 처리
        handleDeepLink(intent)

        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, ConnectActivity::class.java))
            finish()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    // ✅ 딥링크 처리 → code 추출
    private fun handleDeepLink(intent: Intent?) {
        val data = intent?.data ?: return
        val code = data.getQueryParameter("code")

        if (!code.isNullOrBlank()) {
            Log.d("OAuth", "받은 code: $code")
            exchangeCodeForTokens(code)
        } else {
            Toast.makeText(this, "인증 코드가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ 앱에서 직접 구글 서버에 토큰 요청
    private fun exchangeCodeForTokens(code: String) {
        lifecycleScope.launch {
            try {
                val tokenResponse = getGoogleTokens(code)
                if (tokenResponse != null) {
                    sendTokensToBackend(tokenResponse.access_token, tokenResponse.refresh_token)
                } else {
                    Toast.makeText(this@CalendarConnectActivity, "구글 토큰 요청 실패", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("OAuth", "토큰 요청 중 오류", e)
                Toast.makeText(this@CalendarConnectActivity, "오류 발생", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ✅ Google OAuth 토큰 요청 함수
    private suspend fun getGoogleTokens(code: String): GoogleTokenResponse? = withContext(Dispatchers.IO) {
        val url = "https://oauth2.googleapis.com/token"

        val formBody = FormBody.Builder()
            .add("code", code)
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .add("redirect_uri", redirectUri)
            .add("grant_type", "authorization_code")
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        val client = OkHttpClient()
        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val body = response.body?.string()
            Gson().fromJson(body, GoogleTokenResponse::class.java)
        } else {
            Log.e("OAuth", "구글 서버 응답 오류: ${response.code}")
            null
        }
    }

    // ✅ access_token, refresh_token → 백엔드에 전달
    private fun sendTokensToBackend(accessToken: String, refreshToken: String) {
        lifecycleScope.launch {
            try {
                val request = GoogleRegisterRequest(
                    access_token = accessToken,
                    refresh_token = refreshToken
                )

                val response = RetrofitClient.googleApi.registerGoogleToken(request)
                if (response.isSuccessful) {
                    val jwt = response.body()?.jwt
                    if (!jwt.isNullOrBlank()) {
                        TokenManager.saveJwtToken(this@CalendarConnectActivity, jwt)
                        RetrofitClient.setAccessToken(jwt)

                        binding.layoutConnectPrompt.visibility = View.GONE
                        binding.layoutConnectDone.visibility = View.VISIBLE
                        Toast.makeText(this@CalendarConnectActivity, "연동 완료", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@CalendarConnectActivity, "JWT가 없습니다", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@CalendarConnectActivity, "JWT 등록 실패", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("OAuth", "백엔드 연동 오류", e)
                Toast.makeText(this@CalendarConnectActivity, "연동 중 오류", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
