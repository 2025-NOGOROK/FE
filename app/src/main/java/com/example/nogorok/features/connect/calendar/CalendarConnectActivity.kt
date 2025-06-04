package com.example.nogorok.features.connect.calendar

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nogorok.databinding.ActivityCalendarConnectBinding
import com.example.nogorok.features.connect.ConnectActivity
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.utils.TokenManager
import kotlinx.coroutines.launch

class CalendarConnectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarConnectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ⬅️ 상단 뒤로가기
        binding.btnBack.setOnClickListener {
            finish()
        }

        // ✅ "네, 연동할래요" 클릭 시 → 구글 로그인 화면으로 이동
        binding.btnConnect.setOnClickListener {
            val googleAuthUrl = Uri.parse(
                "https://accounts.google.com/o/oauth2/v2/auth?" +
                        "client_id=884352206745-907bgce315k7mc4n44om1537tvro6t0f.apps.googleusercontent.com&" +
                        "redirect_uri=https://recommend.ai.kr/auth/google/callback&" +
                        "response_type=code&"  +
                        "scope=https://www.googleapis.com/auth/calendar%20https://www.googleapis.com/auth/userinfo.email&" +
                        "access_type=offline&" +
                        "prompt=consent"
            )
            startActivity(Intent(Intent.ACTION_VIEW, googleAuthUrl))
        }

        // ❌ "아니요" 클릭 시 → 바로 ConnectActivity 이동
        binding.btnNew.setOnClickListener {
            startActivity(Intent(this, ConnectActivity::class.java))
            finish()
        }

        // ✅ 앱이 딥링크로 실행된 경우 처리
        handleDeepLink(intent)

        // ⏭️ 연동 완료 후 "다음" 버튼
        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, ConnectActivity::class.java))
            finish()
        }
    }

    // ✅ 딥링크로부터 token 또는 code 파라미터 처리
    private fun handleDeepLink(intent: Intent?) {
        val data = intent?.data ?: return

        val token = data.getQueryParameter("token")
        val code = data.getQueryParameter("code")

        when {
            token != null -> {
                handleTokenFromRedirect(token)
            }
            code != null -> {
                handleGoogleCallback(code)
            }
        }
    }

    // ✅ 백엔드에서 전달받은 Google access token 저장
    private fun handleTokenFromRedirect(token: String) {
        TokenManager.saveGoogleToken(this, token)
        binding.layoutConnectPrompt.visibility = View.GONE
        binding.layoutConnectDone.visibility = View.VISIBLE
    }

    // ✅ 앱이 code를 직접 받아서 서버에 token 요청할 경우
    private fun handleGoogleCallback(code: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.googleApi.getToken(code)
                if (response.isSuccessful) {
                    val token = response.body()?.access_token
                    if (token != null) {
                        TokenManager.saveGoogleToken(this@CalendarConnectActivity, token)
                    }
                    binding.layoutConnectPrompt.visibility = View.GONE
                    binding.layoutConnectDone.visibility = View.VISIBLE
                } else {
                    Toast.makeText(this@CalendarConnectActivity, "연동 실패", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@CalendarConnectActivity, "오류 발생", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ✅ 앱이 이미 켜져 있는 상태에서 딥링크가 들어올 경우
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }
}
