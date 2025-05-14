// 파일 위치: app/src/main/java/com/example/nogorok/SurveyFinalActivity.kt
package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity

class SurveyFinalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_final)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)

        // 백버튼: 이전 화면으로 이동
        btnBack.setOnClickListener {
            finish()
        }

        // 다음 버튼: 원하는 페이지로 이동(아직 연결할 페이지 없으면 TODO)
        btnNext.setOnClickListener {

            val intent = Intent(this, StressLoadingActivity::class.java)
            startActivity(intent)

        }
    }
}