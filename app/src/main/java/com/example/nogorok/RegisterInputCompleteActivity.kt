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
            // 설문1 페이지로 이동
            val intent = Intent(this, SurveyStep1Activity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
