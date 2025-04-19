package com.example.nogorok

import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class RegisterTermsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_terms)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val chkAll = findViewById<CheckBox>(R.id.chkAll)
        val chkService = findViewById<CheckBox>(R.id.chkService)
        val chkPrivacy = findViewById<CheckBox>(R.id.chkPrivacy)
        val chkHealth = findViewById<CheckBox>(R.id.chkHealth)
        val chkLocation = findViewById<CheckBox>(R.id.chkLocation)
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)

        val allChecks = listOf(chkService, chkPrivacy, chkHealth, chkLocation)

        // 전체 동의 체크 시 하위 항목 모두 체크/해제
        chkAll.setOnCheckedChangeListener { _, isChecked ->
            allChecks.forEach { it.setOnCheckedChangeListener(null) }
            allChecks.forEach { it.isChecked = isChecked }
            allChecks.forEach { chk ->
                chk.setOnCheckedChangeListener { _, _ ->
                    chkAll.isChecked = allChecks.all { it.isChecked }
                    updateButtonState(btnNext, allChecks)
                }
            }
            updateButtonState(btnNext, allChecks)
        }

        // 개별 체크 시 전체 동의 상태도 반영
        allChecks.forEach { chk ->
            chk.setOnCheckedChangeListener { _, _ ->
                chkAll.setOnCheckedChangeListener(null)
                chkAll.isChecked = allChecks.all { it.isChecked }
                chkAll.setOnCheckedChangeListener { _, isChecked ->
                    allChecks.forEach { it.setOnCheckedChangeListener(null) }
                    allChecks.forEach { it.isChecked = isChecked }
                    allChecks.forEach { chk2 ->
                        chk2.setOnCheckedChangeListener { _, _ ->
                            chkAll.isChecked = allChecks.all { it.isChecked }
                            updateButtonState(btnNext, allChecks)
                        }
                    }
                    updateButtonState(btnNext, allChecks)
                }
                updateButtonState(btnNext, allChecks)
            }
        }

        // 초기 버튼 상태
        updateButtonState(btnNext, allChecks)

        // 뒤로가기 버튼
        btnBack.setOnClickListener { finish() }

        // 다음 버튼 클릭 시 (회원가입 정보 입력 화면으로 이동)
        btnNext.setOnClickListener {
            // TODO: 회원가입 정보 입력 화면으로 이동
        }
    }

    // 버튼 활성화/비활성화 및 색상
    private fun updateButtonState(button: MaterialButton, checks: List<CheckBox>) {
        val allChecked = checks.all { it.isChecked }
        button.isEnabled = allChecked
        if (allChecked) {
            button.setBackgroundColor(Color.parseColor("#73605A"))
        } else {
            button.setBackgroundColor(Color.parseColor("#B373605A"))
        }
    }
}
