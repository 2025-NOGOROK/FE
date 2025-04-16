package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 각 스플래시 화면 레이아웃 참조
        val splashLogo = findViewById<LinearLayout>(R.id.splash_logo)
        val splash1 = findViewById<LinearLayout>(R.id.splash1)
        val splash2 = findViewById<LinearLayout>(R.id.splash2)
        val splash3 = findViewById<LinearLayout>(R.id.splash3)

        // 각 시작하기 버튼 참조
        val btnStart1 = findViewById<Button>(R.id.btn_start1)
        val btnStart2 = findViewById<Button>(R.id.btn_start2)
        val btnStart3 = findViewById<Button>(R.id.btn_start3)

        // 1초 동안 로고 화면 보여주기
        Handler(Looper.getMainLooper()).postDelayed({
            splashLogo.visibility = View.GONE
            splash1.visibility = View.VISIBLE
        }, 1000)

        // 첫 번째 시작하기 버튼 클릭 시 두 번째 스플래시 화면으로 전환
        btnStart1.setOnClickListener {
            splash1.visibility = View.GONE
            splash2.visibility = View.VISIBLE
        }

        // 두 번째 시작하기 버튼 클릭 시 세 번째 스플래시 화면으로 전환
        btnStart2.setOnClickListener {
            splash2.visibility = View.GONE
            splash3.visibility = View.VISIBLE
        }

        // 세 번째 시작하기 버튼 클릭 시 로그인/회원가입 선택 화면으로 이동
        btnStart3.setOnClickListener {
            val intent = Intent(this, SignupLoginActivity::class.java) // 여기서 로그인/회원가입 화면으로 이동
            startActivity(intent)
            finish() // 스플래시 액티비티 종료
        }
    }
}
