package com.example.nogorok.features.connect.calendar

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nogorok.MainActivity
import com.example.nogorok.databinding.ActivityCalendarConnectBinding
import com.example.nogorok.features.connect.ConnectActivity
import com.example.nogorok.utils.TokenManager
import kotlinx.coroutines.launch

class CalendarConnectActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MODE = "MODE"
        const val MODE_SIGN_UP = "SIGN_UP"   // 최초 연동(회원가입 플로우)
        const val MODE_RELINK  = "RELINK"    // 로그인 중 401 재연동 플로우
        const val EXTRA_AUTH_URL = "AUTH_URL" // 서버가 내려준 동의 URL(선택)
    }

    private lateinit var binding: ActivityCalendarConnectBinding

    private val clientId = "1062370068118-f2cgskgioh5eeb14rqq5di0tsn30c1et.apps.googleusercontent.com"
    private val redirectUri = "https://recommend.ai.kr/auth/google/callback"

    private var mode: String = MODE_SIGN_UP
    private var serverAuthUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mode = intent.getStringExtra(EXTRA_MODE) ?: MODE_SIGN_UP
        serverAuthUrl = intent.getStringExtra(EXTRA_AUTH_URL)

        applyModeUi(mode)

        binding.btnBack.setOnClickListener { finish() }

        binding.btnConnect.setOnClickListener { openConsent(serverAuthUrl) }

        binding.btnNew.setOnClickListener {
            // 회원가입 모드에서만 보임
            startActivity(Intent(this, ConnectActivity::class.java))
            finish()
        }

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
                binding.btnNew.visibility = View.GONE // 401일 땐 "아니요" 숨김
                // 원하면 자동 오픈:
                // openConsent(serverAuthUrl)
            }
            else -> binding.btnNew.visibility = View.VISIBLE
        }
    }

    private fun openConsent(authUrlFromServer: String?) {
        val uri = authUrlFromServer?.let { Uri.parse(it) } ?: buildDefaultAuthUri()
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun handleDeepLink(intent: Intent?) {
        val data = intent?.data ?: return
        Log.d("OAuth", "딥링크 URI: $data")

        // intent://oauth2callback?jwt=... -> 앱에서: com.example.nogorok://oauth2callback?jwt=...
        if (data.scheme == "com.example.nogorok" && data.host == "oauth2callback") {
            val jwt = data.getQueryParameter("jwt")
            if (!jwt.isNullOrBlank()) {
                TokenManager.saveJwtToken(this, jwt)
                Toast.makeText(this, "구글 연동 완료", Toast.LENGTH_SHORT).show()

                // 필요하면 서버 최종 점검:
                lifecycleScope.launch {
                    // runCatching { RetrofitClient.authApi.ensureGoogleLinked() }
                }

                // ✅ 성공 후 분기
                if (mode == MODE_RELINK) {
                    // 재연동: 홈으로
                    startActivity(
                        Intent(this, MainActivity::class.java).apply {
                            // 백스택에 ConnectActivity 남지 않도록 정리
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            // 메인에서 홈으로 이동시키는 힌트
                            putExtra(MainActivity.EXTRA_NAV_DEST, MainActivity.NAV_HOME)
                        }
                    )
                } else {
                    // 회원가입: 기존처럼 연결 화면으로
                    startActivity(
                        Intent(this, ConnectActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        }
                    )
                }
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
