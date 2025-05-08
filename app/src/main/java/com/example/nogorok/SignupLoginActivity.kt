package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class SignupLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_login)

        // 회원가입 버튼 클릭 시 약관동의 페이지로 이동!
        findViewById<MaterialButton>(R.id.btnSignup).setOnClickListener {
            // RegisterTermsActivity로 이동
            val intent = Intent(this, RegisterTermsActivity::class.java)
            startActivity(intent)
        }

        // 로그인 버튼 클릭 시 로그인 페이지로 이동
        findViewById<MaterialButton>(R.id.btnLogin).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
