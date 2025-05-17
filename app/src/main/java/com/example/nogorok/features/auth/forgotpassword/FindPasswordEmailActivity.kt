package com.example.nogorok.features.auth.forgotpassword

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.nogorok.R
import com.google.android.material.button.MaterialButton

class FindPasswordEmailActivity : AppCompatActivity() {

    private var isValidationActivated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_password_email)

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val emailError = findViewById<TextView>(R.id.emailError)
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        // 색상 리소스 불러오기
        val colorFull = ContextCompat.getColor(this, R.color.primary)      // #73605A
        val color70 = ContextCompat.getColor(this, R.color.primary_70)     // #B373605A

        // 처음엔 입력란이 비어있으니 70% 투명도 적용
        btnNext.backgroundTintList = ColorStateList.valueOf(color70)

        // 이메일 입력란에 텍스트가 바뀔 때마다 버튼 배경색 변경
        edtEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // 입력값이 있으면 진한색, 없으면 연한색
                val isNotEmpty = !s.isNullOrEmpty()
                btnNext.backgroundTintList = ColorStateList.valueOf(
                    if (isNotEmpty) colorFull else color70
                )
                // 기존 유효성 에러 표시 로직은 그대로 둬
                if (isValidationActivated) {
                    emailError.visibility = if (s.isNullOrEmpty()) TextView.VISIBLE else TextView.GONE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnNext.setOnClickListener {
            isValidationActivated = true
            val email = edtEmail.text?.toString()?.trim() ?: ""
            if (email.isEmpty()) {
                emailError.visibility = TextView.VISIBLE
            } else {
                emailError.visibility = TextView.GONE
                // 임시: 이메일이 "nogorok@gmail.com"일 때만 성공
                if (email == "nogorok@gmail.com") {
                    val intent = Intent(this, FindPasswordResetActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                } else {
                    AlertDialog.Builder(this)
                        .setMessage("일치하는 회원정보가 없습니다.")
                        .setPositiveButton("확인", null)
                        .show()
                }
            }
        }
    }
}
