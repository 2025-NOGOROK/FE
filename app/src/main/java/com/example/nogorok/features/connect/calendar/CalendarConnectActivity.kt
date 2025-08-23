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
import com.example.nogorok.utils.TokenManager
import kotlinx.coroutines.launch

class CalendarConnectActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MODE = "MODE"
        const val MODE_SIGN_UP = "SIGN_UP"   // 최초 연동(회원가입)
        const val MODE_RELINK  = "RELINK"    // 401 재연동

        const val EXTRA_AUTH_URL = "AUTH_URL" // 서버가 내려준 동의 URL(선택)
    }

    private lateinit var binding: ActivityCalendarConnectBinding

    // 참고: clientSecret은 앱에 두지 마세요(서버 전용).
    private val clientId = "1062370068118-f2cgskgioh5eeb14rqq5di0tsn30c1et.apps.googleusercontent.com"
    private val redirectUri = "https://recommend.ai.kr/auth/google/callback"

    private var mode: String = MODE_SIGN_UP
    private var serverAuthUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 모드/URL 파라미터
        mode = intent.getStringExtra(EXTRA_MODE) ?: MODE_SIGN_UP
        serverAuthUrl = intent.getStringExtra(EXTRA_AUTH_URL)

        // UI 분기
        applyModeUi(mode)

        // 뒤로가기
        binding.btnBack.setOnClickListener { finish() }

        // “연결하기”
        binding.btnConnect.setOnClickListener {
            openConsent(serverAuthUrl)
        }

        // “아니요” (회원가입 모드에서만 보임)
        binding.btnNew.setOnClickListener {
            startActivity(Intent(this, ConnectActivity::class.java))
            finish()
        }

        // 딥링크 처리
        handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun applyModeUi(mode: String) {
        when (mode) {
            MODE_RELINK -> {
                // 401 재연동: '아니요' 숨김, (원하면) 뒤로가기도 숨길 수 있음
                binding.btnNew.visibility = View.GONE
                // binding.btnBack.visibility = View.GONE // 필요하면 주석 해제

                // 문구도 재연동에 맞게 바꾸고 싶다면 레이아웃 id에 맞춰 바꾸세요.
                // binding.title.text = "구글 권한 재연동"
                // binding.subtitle.text = "만료된 구글 권한을 다시 연결해 주세요."

                // UX를 더 매끄럽게: 화면 진입 시 자동으로 동의창 열기 원하면 주석 해제
                // openConsent(serverAuthUrl)
            }
            else -> {
                // 회원가입(최초 연동): 기본 그대로
                binding.btnNew.visibility = View.VISIBLE
            }
        }
    }

    private fun openConsent(authUrlFromServer: String?) {
        val uri = authUrlFromServer?.let { Uri.parse(it) } ?: buildDefaultAuthUri()
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun handleDeepLink(intent: Intent?) {
        val data = intent?.data ?: return
        Log.d("OAuth", "딥링크 URI: $data")

        // 백엔드: intent://oauth2callback?jwt=...#Intent;scheme=com.example.nogorok;package=com.example.nogorok;end
        // 앱에서 수신: com.example.nogorok://oauth2callback?jwt=...
        if (data.scheme == "com.example.nogorok" && data.host == "oauth2callback") {
            val jwt = data.getQueryParameter("jwt")
            if (!jwt.isNullOrBlank()) {
                Log.d("OAuth", "받은 JWT: $jwt")
                TokenManager.saveJwtToken(this, jwt)

                Toast.makeText(this, "구글 연동 완료", Toast.LENGTH_SHORT).show()

                // (선택) 서버 최종 점검 호출이 필요하면 여기에
                lifecycleScope.launch {
                    // runCatching { RetrofitClient.authApi.ensureGoogleLinked() }
                }

                // 어디로 돌아갈지: 가입 플로우/설정 화면 등 제품 정책에 맞게
                startActivity(Intent(this, ConnectActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                })
                finish()
            } else {
                Toast.makeText(this, "JWT가 없습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buildDefaultAuthUri(): Uri {
        val scopeList = listOf(
            "https://www.googleapis.com/auth/calendar",
            "https://www.googleapis.com/auth/calendar.events",
            "https://www.googleapis.com/auth/calendar.freebusy",
            "https://www.googleapis.com/auth/userinfo.email",
            "https://www.googleapis.com/auth/userinfo.profile"
        )
        return Uri.parse(
            "https://accounts.google.com/o/oauth2/v2/auth?" +
                    "client_id=$clientId&" +
                    "redirect_uri=$redirectUri&" +
                    "response_type=code&" +
                    "scope=${scopeList.joinToString("%20")}&" +
                    "access_type=offline&" +
                    "include_granted_scopes=true&" +
                    "prompt=consent"
        )
    }
}
