// 파일 위치: app/src/main/java/com/example/nogorok/SurveyStep6Activity.kt
package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity

class SurveyStep6Activity : AppCompatActivity() {

    // 0: 선택 안함, 1: 예, 2: 아니요
    private var selectedOption: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_step6)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnYes = findViewById<MaterialButton>(R.id.btnLoose)   // "예" 버튼
        val btnNo = findViewById<MaterialButton>(R.id.btnTight)    // "아니요" 버튼
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)

        btnYes.isSelected = false
        btnNo.isSelected = false
        btnNext.isEnabled = false

        btnBack.setOnClickListener {
            finish()
        }

        btnYes.setOnClickListener {
            if (selectedOption == 1) {
                selectedOption = 0
                btnYes.isSelected = false
                btnNo.isSelected = false
            } else {
                selectedOption = 1
                btnYes.isSelected = true
                btnNo.isSelected = false
            }
            updateNextButtonState(btnNext)
        }

        btnNo.setOnClickListener {
            if (selectedOption == 2) {
                selectedOption = 0
                btnYes.isSelected = false
                btnNo.isSelected = false
            } else {
                selectedOption = 2
                btnYes.isSelected = false
                btnNo.isSelected = true
            }
            updateNextButtonState(btnNext)
        }

        btnNext.setOnClickListener {
            when (selectedOption) {
                1 -> { // 예 버튼 선택 시
                    val intent = Intent(this, SurveyStep7Activity::class.java)
                    startActivity(intent)
                }
                2 -> { // 아니요 버튼 선택 시
                    val intent = Intent(this, SurveyFinalActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun updateNextButtonState(btnNext: MaterialButton) {
        btnNext.isEnabled = (selectedOption == 1 || selectedOption == 2)
    }
}