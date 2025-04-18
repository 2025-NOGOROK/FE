package com.example.nogorok

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class FindPasswordResetActivity : AppCompatActivity() {

    // 눈 아이콘 상태 변수
    private var isNewPwVisible = false
    private var isConfirmPwVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_password_reset)

        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val edtNewPassword = findViewById<EditText>(R.id.edtNewPassword)
        val edtConfirmPassword = findViewById<EditText>(R.id.edtConfirmPassword)
        val btnToggleNewPassword = findViewById<ImageButton>(R.id.btnToggleNewPassword)
        val btnToggleConfirmPassword = findViewById<ImageButton>(R.id.btnToggleConfirmPassword)
        val passwordMatchMessage = findViewById<TextView>(R.id.passwordMatchMessage)
        val btnConfirm = findViewById<MaterialButton>(R.id.btnConfirm)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        // 이메일 고정 표시
        val email = intent.getStringExtra("email") ?: ""
        tvEmail.text = email

        btnBack.setOnClickListener { finish() }

        // 새 비밀번호 입력 시 항상 가리기
        edtNewPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        edtNewPassword.setSelection(edtNewPassword.text?.length ?: 0)
        btnToggleNewPassword.setImageResource(R.drawable.ic_eye_off)

        // 비밀번호 재입력도 항상 가리기
        edtConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        edtConfirmPassword.setSelection(edtConfirmPassword.text?.length ?: 0)
        btnToggleConfirmPassword.setImageResource(R.drawable.ic_eye_off)

        // 새 비밀번호 눈 버튼 클릭
        btnToggleNewPassword.setOnClickListener {
            isNewPwVisible = !isNewPwVisible
            if (isNewPwVisible) {
                edtNewPassword.inputType = InputType.TYPE_CLASS_TEXT
                btnToggleNewPassword.setImageResource(R.drawable.ic_eye_on)
            } else {
                edtNewPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                btnToggleNewPassword.setImageResource(R.drawable.ic_eye_off)
            }
            edtNewPassword.setSelection(edtNewPassword.text?.length ?: 0)
        }

        // 비밀번호 재입력 눈 버튼 클릭
        btnToggleConfirmPassword.setOnClickListener {
            isConfirmPwVisible = !isConfirmPwVisible
            if (isConfirmPwVisible) {
                edtConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT
                btnToggleConfirmPassword.setImageResource(R.drawable.ic_eye_on)
            } else {
                edtConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                btnToggleConfirmPassword.setImageResource(R.drawable.ic_eye_off)
            }
            edtConfirmPassword.setSelection(edtConfirmPassword.text?.length ?: 0)
        }

        // 새 비밀번호 입력 시 항상 가리기(눈 버튼으로만 토글)
        edtNewPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isNewPwVisible) {
                    edtNewPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    btnToggleNewPassword.setImageResource(R.drawable.ic_eye_off)
                    edtNewPassword.setSelection(edtNewPassword.text?.length ?: 0)
                }
                updatePasswordMatch()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 비밀번호 재입력 입력 시 항상 가리기(눈 버튼으로만 토글)
        edtConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isConfirmPwVisible) {
                    edtConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    btnToggleConfirmPassword.setImageResource(R.drawable.ic_eye_off)
                    edtConfirmPassword.setSelection(edtConfirmPassword.text?.length ?: 0)
                }
                updatePasswordMatch()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // 비밀번호 일치/불일치 메시지 처리
    private fun updatePasswordMatch() {
        val pw = findViewById<EditText>(R.id.edtNewPassword).text?.toString() ?: ""
        val confirm = findViewById<EditText>(R.id.edtConfirmPassword).text?.toString() ?: ""
        val passwordMatchMessage = findViewById<TextView>(R.id.passwordMatchMessage)
        val btnConfirm = findViewById<Button>(R.id.btnConfirm)

        if (confirm.isEmpty()) {
            passwordMatchMessage.visibility = TextView.GONE
            btnConfirm.isEnabled = false
        } else if (pw == confirm) {
            passwordMatchMessage.visibility = TextView.VISIBLE
            passwordMatchMessage.text = "비밀번호가 일치합니다."
            passwordMatchMessage.setTextColor(0xFF4CAF50.toInt()) // 초록색
            btnConfirm.isEnabled = true
        } else {
            passwordMatchMessage.visibility = TextView.VISIBLE
            passwordMatchMessage.text = "비밀번호가 일치하지 않습니다."
            passwordMatchMessage.setTextColor(0xFFD32F2F.toInt()) // 빨간색
            btnConfirm.isEnabled = false
        }
    }
}
