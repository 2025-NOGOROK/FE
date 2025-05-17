package com.example.nogorok.features.auth.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nogorok.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 로그인 프래그먼트를 이 액티비티에 연결
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.loginFragmentContainer, LoginFragment())
                .commit()
        }
    }
}
