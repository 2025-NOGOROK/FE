package com.example.nogorok

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class RegisterTermsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_terms)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val chkAll = findViewById<CheckBox>(R.id.chkAll)
        val chkService = findViewById<CheckBox>(R.id.chkService)
        val chkPrivacy = findViewById<CheckBox>(R.id.chkPrivacy)
        val chkHealth = findViewById<CheckBox>(R.id.chkHealth)
        val chkLocation = findViewById<CheckBox>(R.id.chkLocation)
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)

        val allChecks = listOf(chkService, chkPrivacy, chkHealth, chkLocation)

        // 전체 동의 체크 시 하위 항목 모두 체크/해제
        chkAll.setOnCheckedChangeListener { _, isChecked ->
            // 리스너 중복 방지
            allChecks.forEach { it.setOnCheckedChangeListener(null) }
            allChecks.forEach { it.isChecked = isChecked }
            // 리스너 재설정
            allChecks.forEach { chk ->
                chk.setOnCheckedChangeListener { _, _ -> updateAllCheck(chkAll, allChecks, btnNext) }
            }
            updateButtonState(btnNext, allChecks)
        }

        // 개별 체크 시 전체 동의 상태도 반영
        allChecks.forEach { chk ->
            chk.setOnCheckedChangeListener { _, _ ->
                updateAllCheck(chkAll, allChecks, btnNext)
            }
        }

        // 초기 버튼 상태
        updateButtonState(btnNext, allChecks)

        btnNext.setOnClickListener {
            val intent = Intent(this, RegisterInputNameActivity::class.java)
            startActivity(intent)
        }
    }

    // 개별 체크박스 변경 시 전체동의 상태 업데이트
    private fun updateAllCheck(
        chkAll: CheckBox,
        allChecks: List<CheckBox>,
        btnNext: MaterialButton
    ) {
        val allChecked = allChecks.all { it.isChecked }
        // 하위 4개가 모두 체크되면 전체동의도 체크
        chkAll.setOnCheckedChangeListener(null)
        chkAll.isChecked = allChecked
        chkAll.setOnCheckedChangeListener { _, isChecked ->
            allChecks.forEach { it.setOnCheckedChangeListener(null) }
            allChecks.forEach { it.isChecked = isChecked }
            allChecks.forEach { chk ->
                chk.setOnCheckedChangeListener { _, _ -> updateAllCheck(chkAll, allChecks, btnNext) }
            }
            updateButtonState(btnNext, allChecks)
        }
        updateButtonState(btnNext, allChecks)
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
