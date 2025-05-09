package com.example.nogorok

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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

        // 버튼 활성/비활성 실시간 제어
        edtEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // 입력값이 있으면 활성화, 없으면 비활성화
                btnNext.isEnabled = !s.isNullOrEmpty()

                if (isValidationActivated) {
                    emailError.visibility = if (s.isNullOrEmpty()) TextView.VISIBLE else TextView.GONE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 최초 진입 시 버튼 비활성화
        btnNext.isEnabled = false

        btnNext.setOnClickListener {
            isValidationActivated = true
            val email = edtEmail.text?.toString()?.trim() ?: ""
            if (email.isEmpty()) {
                emailError.visibility = TextView.VISIBLE
            } else {
                emailError.visibility = TextView.GONE
                // 여기서 실제 회원정보 체크(임시로 이메일이 "nogorok@gmail.com"만 성공)
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
