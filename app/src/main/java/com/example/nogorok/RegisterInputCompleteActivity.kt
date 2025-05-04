package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class RegisterInputCompleteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_input_complete)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val btnSurvey = findViewById<MaterialButton>(R.id.btnSurvey)
        btnSurvey.setOnClickListener {
            // 설문1 페이지로 이동
            val intent = Intent(this, SurveyStep1Activity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
