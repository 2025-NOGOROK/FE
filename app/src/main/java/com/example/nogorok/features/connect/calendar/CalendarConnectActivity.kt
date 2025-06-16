package com.example.nogorok.features.connect.calendar

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nogorok.databinding.ActivityCalendarConnectBinding
import com.example.nogorok.features.connect.ConnectActivity
import com.example.nogorok.utils.TokenManager

class CalendarConnectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarConnectBinding

    private val clientId = "1062370068118-f2cgskgioh5eeb14rqq5di0tsn30c1et.apps.googleusercontent.com"
    private val clientSecret = "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"
    private val redirectUri = "https://recommend.ai.kr/auth/google/callback"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로가기 버튼
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Google OAuth 시작
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

        // "아니요" → 뒤로가기
        binding.btnNew.setOnClickListener {
            startActivity(Intent(this, ConnectActivity::class.java))
            finish()
        }

        // 딥링크 처리
        handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val data = intent?.data
        Log.d("OAuth", "딥링크 URI: $data")

        val jwt = data?.getQueryParameter("jwt")
        if (!jwt.isNullOrBlank()) {
            Log.d("OAuth", "받은 JWT: $jwt")
            TokenManager.saveJwtToken(this, jwt)

            Toast.makeText(this, "구글 연동 완료", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ConnectActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "JWT가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }
}
