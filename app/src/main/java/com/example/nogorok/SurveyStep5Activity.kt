// 파일 위치: app/src/main/java/com/example/nogorok/SurveyStep5Activity.kt
package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity

class SurveyStep5Activity : AppCompatActivity() {

    // 선택된 버튼 인덱스 (-1은 미선택)
    private var selectedIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_step5)

        // 뒤로가기 버튼
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        // 질문 텍스트
        val tvQuestion = findViewById<TextView>(R.id.tvQuestion)
        tvQuestion.text = "스트레스를 받으면\n어떤 반응을 보이나요?"

        // 5개 버튼 찾기
        val btnStress1 = findViewById<MaterialButton>(R.id.btnStress1)
        val btnStress2 = findViewById<MaterialButton>(R.id.btnStress2)
        val btnStress3 = findViewById<MaterialButton>(R.id.btnStress3)
        val btnStress4 = findViewById<MaterialButton>(R.id.btnStress4)
        val btnStress5 = findViewById<MaterialButton>(R.id.btnStress5)
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)
        btnNext.isEnabled = false

        val btnList = listOf(btnStress1, btnStress2, btnStress3, btnStress4, btnStress5)

        // 단일 선택 + 토글(선택 해제) 로직
        btnList.forEachIndexed { idx, btn ->
            btn.setOnClickListener {
                if (selectedIndex == idx) {
                    // 이미 선택된 버튼을 다시 누르면 선택 해제
                    btn.isSelected = false
                    selectedIndex = -1
                    btnNext.isEnabled = false
                } else {
                    // 새로운 버튼을 선택
                    btnList.forEach { it.isSelected = false }
                    btn.isSelected = true
                    selectedIndex = idx
                    btnNext.isEnabled = true
                }
            }
        }

        // 다음 버튼 클릭 시
        btnNext.setOnClickListener {
            if (selectedIndex != -1) {
                val intent = Intent(this, SurveyStep6Activity::class.java)
                // 선택값 넘기려면 아래처럼 사용 가능
                // intent.putExtra("stressReaction", selectedIndex)
                startActivity(intent)
            }
        }
    }
}
