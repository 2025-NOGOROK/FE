package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity

class SurveyStep3Activity : AppCompatActivity() {

    // 0: 선택 안함, 1: 예, 2: 아니요
    private var selectedOption: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_step3) //

        // 각 뷰 찾아오기
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnYes = findViewById<MaterialButton>(R.id.btnLoose)   // "아침형 인간" 버튼 (id는 btnLoose 그대로 사용)
        val btnNo = findViewById<MaterialButton>(R.id.btnTight)    // "저녁형 인간" 버튼 (id는 btnTight 그대로 사용)
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)

        // 초기 상태: 모두 미선택, 다음 버튼 비활성화
        btnYes.isSelected = false
        btnNo.isSelected = false
        btnNext.isEnabled = false

        // 뒤로가기 버튼: 이전 페이지로 이동
        btnBack.setOnClickListener {
            finish() // 이전 액티비티로 돌아감
        }

        // "아침형 인간" 버튼 클릭 시 (토글)
        btnYes.setOnClickListener {
            if (selectedOption == 1) {
                // 이미 선택된 상태면 해제
                selectedOption = 0
                btnYes.isSelected = false
                btnNo.isSelected = false
            } else {
                // 선택
                selectedOption = 1
                btnYes.isSelected = true
                btnNo.isSelected = false
            }
            updateNextButtonState(btnNext)
        }

        // "저녁형 인간" 버튼 클릭 시 (토글)
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

        // 다음 버튼 클릭 시 다음 설문4 페이지로 이동
        btnNext.setOnClickListener {
            if (selectedOption == 1 || selectedOption == 2) {
                val intent = Intent(this, SurveyStep4Activity::class.java)
                // 선택값을 넘기고 싶으면 putExtra로 전달 가능!
                // intent.putExtra("changeSchedule", selectedOption)
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