package com.example.nogorok.features.auth.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nogorok.R
import com.example.nogorok.databinding.ActivitySignupLoginBinding
import android.content.Intent
import com.example.nogorok.features.auth.login.LoginFragment
import com.example.nogorok.features.auth.register.RegisterTermsActivity

class SignupLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // "회원가입" 버튼 클릭 시 -> RegisterActivity(Fragment 구조)로 이동
        binding.btnSignup.setOnClickListener {
            startActivity(Intent(this, RegisterTermsActivity::class.java))
        }

        // "로그인" 버튼 클릭 시 -> LoginFragment 로 전환
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
