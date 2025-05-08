package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {

    private var isValidationActivated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val forgotPw = findViewById<TextView>(R.id.tvForgotPassword)
        forgotPw.setOnClickListener {
            val intent = Intent(this, FindPasswordEmailActivity::class.java)
            startActivity(intent)
        }
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val emailError = findViewById<TextView>(R.id.emailError)
        val passwordError = findViewById<TextView>(R.id.passwordError)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        // 뒤로가기 버튼 클릭 시
        btnBack.setOnClickListener {
            finish()
        }

        // EditText 실시간 유효성 체크 리스너
        val emailWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isValidationActivated) {
                    if (s.isNullOrEmpty()) {
                        emailError.visibility = TextView.VISIBLE
                    } else {
                        emailError.visibility = TextView.GONE
                    }
                }
            }
        }

        val passwordWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isValidationActivated) {
                    if (s.isNullOrEmpty()) {
                        passwordError.visibility = TextView.VISIBLE
                    } else {
                        passwordError.visibility = TextView.GONE
                    }
                }
            }
        }

        edtEmail.addTextChangedListener(emailWatcher)
        edtPassword.addTextChangedListener(passwordWatcher)

        // 로그인 버튼 클릭 시 유효성 체크 및 메인페이지 이동
        btnLogin.setOnClickListener {
            isValidationActivated = true

            val email = edtEmail.text?.toString()?.trim() ?: ""
            val password = edtPassword.text?.toString()?.trim() ?: ""

            var isValid = true

            if (email.isEmpty()) {
                emailError.visibility = TextView.VISIBLE
                isValid = false
            } else {
                emailError.visibility = TextView.GONE
            }

            if (password.isEmpty()) {
                passwordError.visibility = TextView.VISIBLE
                isValid = false
            } else {
                passwordError.visibility = TextView.GONE
            }

            if (isValid) {
                // 로그인 처리
                Toast.makeText(this, "로그인 시도: $email", Toast.LENGTH_SHORT).show()

                // 로그인 성공 시 메인페이지로 이동
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
