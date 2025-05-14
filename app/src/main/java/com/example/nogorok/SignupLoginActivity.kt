package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SignupLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_login)

        // 회원가입 버튼 클릭 시 RegisterTermsActivity로 이동
        val btnSignup = findViewById<Button>(R.id.btnSignup)
        btnSignup.setOnClickListener {
            // RegisterTermsActivity로 이동하는 Intent 생성
            val intent = Intent(this, RegisterTermsActivity::class.java)
            startActivity(intent)
        }

        // 로그인 버튼 클릭 시 LoginActivity로 이동
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            // LoginActivity로 이동하는 Intent 생성
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
