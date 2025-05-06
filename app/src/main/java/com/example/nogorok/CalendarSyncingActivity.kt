package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class CalendarSyncingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_syncing)

        // 뒤로가기 버튼 클릭 시 이전 페이지로 이동
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // 이전 페이지로 돌아감
        }

        // 2초 후 자동으로 CalendarConnectSuccessActivity로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, CalendarConnectSuccessActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}
