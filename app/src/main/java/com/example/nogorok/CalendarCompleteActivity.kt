package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CalendarCompleteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_complete)

        // 1. 상단 뒤로가기 버튼 클릭 시 이전 페이지로 이동
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // 이전 페이지로 돌아감
        }

        // 2. 완료하기 버튼 클릭 시 메인 페이지로 이동
        val btnFinish = findViewById<Button>(R.id.btnComplete)
        btnFinish.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // 메인화면으로 이동할 때 스택을 모두 비우고 새로 시작하고 싶으면 아래 플래그 추가
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }
    }
}