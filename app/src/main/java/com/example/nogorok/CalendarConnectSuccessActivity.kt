package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class CalendarConnectSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_connect_success)

        // 뒤로가기 버튼 클릭 시 이전 페이지로 이동
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // 이전 페이지로 돌아감
        }

        // 다음 버튼 클릭 시 CalendarCompleteActivity로 이동
        val btnNext = findViewById<Button>(R.id.btnNext)
        btnNext.setOnClickListener {
            val intent = Intent(this, CalendarCompleteActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
