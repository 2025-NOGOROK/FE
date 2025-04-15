package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginBtn = findViewById<Button>(R.id.btn_login)
        val goRegister = findViewById<TextView>(R.id.tv_go_register)

        loginBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fragment_to_load", "home") // HomeFragment로 이동 정보 전달
            startActivity(intent)
            finish()
        }

        goRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}