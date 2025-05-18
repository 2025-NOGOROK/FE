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
                        "redirect_uri=http://localhost:8080/auth/google/callback&" + // 앱에서 처리하는 딥링크 스킴
                        "response_type=code&" +
                        "scope=https://www.googleapis.com/auth/calendar https://www.googleapis.com/auth/userinfo.email&" +
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

        // ✅ OAuth 인증 후 앱이 실행되면 code 파라미터 처리
        val code = intent?.data?.getQueryParameter("code")
        if (code != null) {
            handleGoogleCallback(code)
        }

        // ⏭️ 연동 완료 후 "다음" 버튼
        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, ConnectActivity::class.java))
            finish()
        }
    }

    private fun handleGoogleCallback(code: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.googleApi.getToken(code)
                if (response.isSuccessful) {
                    // UI 전환: 연동 완료 상태 보여주기
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
}
