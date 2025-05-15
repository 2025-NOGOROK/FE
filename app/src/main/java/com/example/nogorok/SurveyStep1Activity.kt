// 파일 위치: app/src/main/java/com/example/nogorok/SurveyStep1Activity.kt
package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class SurveyStep1Activity : AppCompatActivity() {

    // 0: 선택 안함, 1: 루즈, 2: 타이트
    private var selectedOption: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_step1)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnLoose = findViewById<MaterialButton>(R.id.btnLoose)
        val btnTight = findViewById<MaterialButton>(R.id.btnTight)
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)

        // 처음엔 모두 미선택, 다음 버튼 비활성화
        btnLoose.isSelected = false
        btnTight.isSelected = false
        btnNext.isEnabled = false

        // 백버튼: 회원가입 완료 페이지로 이동
        btnBack.setOnClickListener {
            val intent = Intent(this, RegisterInputCompleteActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        // 루즈한 일정 버튼 클릭 시 (토글)
        btnLoose.setOnClickListener {
            if (selectedOption == 1) {
                // 이미 선택된 상태면 해제
                selectedOption = 0
                btnLoose.isSelected = false
                btnTight.isSelected = false
            } else {
                // 선택
                selectedOption = 1
                btnLoose.isSelected = true
                btnTight.isSelected = false
            }
            updateNextButtonState(btnNext)
        }

        // 타이트한 일정 버튼 클릭 시 (토글)
        btnTight.setOnClickListener {
            if (selectedOption == 2) {
                // 이미 선택된 상태면 해제
                selectedOption = 0
                btnLoose.isSelected = false
                btnTight.isSelected = false
            } else {
                // 선택
                selectedOption = 2
                btnLoose.isSelected = false
                btnTight.isSelected = true
            }
            updateNextButtonState(btnNext)
        }

        // 다음 버튼 클릭 시 설문2로 이동
        btnNext.setOnClickListener {
            if (selectedOption == 1 || selectedOption == 2) {
                val intent = Intent(this, SurveyStep2Activity::class.java)
                // 선택값을 넘기고 싶으면 putExtra로 전달 가능!
                // intent.putExtra("scheduleType", selectedOption)
                startActivity(intent)
            }
        }
    }

    // 다음 버튼 활성/비활성 상태 갱신
    private fun updateNextButtonState(btnNext: MaterialButton) {
        btnNext.isEnabled = (selectedOption == 1 || selectedOption == 2)
        // 색상/테두리/텍스트 컬러는 selector에서 자동 처리!
    }
}