package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class RegisterInputCompleteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_input_complete)

        val btnSurvey = findViewById<MaterialButton>(R.id.btnSurvey)
        btnSurvey.setOnClickListener {
            // 설문화면(또는 메인화면)으로 이동 추후에 설문화면 만들고 바꿔야하는 코드!!!
            // 예시: MainActivity로 이동
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
