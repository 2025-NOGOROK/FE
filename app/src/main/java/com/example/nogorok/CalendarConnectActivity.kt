package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class CalendarConnectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_connect)

        // 상단 뒤로가기 버튼: 이전 페이지로 이동
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // 이전(스트레스 로딩) 페이지로 돌아감
        }

        // "네, 연동할래요." 버튼: 구글 로그인 모달 띄우기
        findViewById<Button>(R.id.btnConnect).setOnClickListener {
            val dialog = GoogleSignInBottomSheet()
            dialog.show(supportFragmentManager, "GoogleSignIn")
        }

        // "아니요, 새롭게 일정을 시작할래요." 버튼: CalendarCompleteActivity로 이동
        findViewById<Button>(R.id.btnNew).setOnClickListener {
            val intent = Intent(this, CalendarCompleteActivity::class.java)
            startActivity(intent)
        }
    }
}