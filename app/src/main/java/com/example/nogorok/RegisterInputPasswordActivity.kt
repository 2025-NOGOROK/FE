package com.example.nogorok

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class RegisterInputPasswordActivity : AppCompatActivity() {

    // 뷰 변수 선언
    private lateinit var edtPassword: EditText
    private lateinit var edtPasswordConfirm: EditText
    private lateinit var btnNext: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_input_password)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        edtPassword = findViewById(R.id.edtPassword)
        edtPasswordConfirm = findViewById(R.id.edtPasswordConfirm)
        btnNext = findViewById(R.id.btnNext)

        // 입력값 감지해서 버튼 활성화
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { updateButtonState() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        edtPassword.addTextChangedListener(watcher)
        edtPasswordConfirm.addTextChangedListener(watcher)

        // 처음 진입 시 버튼 비활성화
        updateButtonState()

        // 다음 버튼 클릭 시 비밀번호 일치 체크
        btnNext.setOnClickListener {
            val pw = edtPassword.text.toString()
            val pw2 = edtPasswordConfirm.text.toString()
            if (pw != pw2) {
                // 불일치 다이얼로그
                AlertDialog.Builder(this)
                    .setMessage("비밀번호가 일치하지 않습니다.")
                    .setPositiveButton("확인", null)
                    .show()
            } else {
                // 다음 단계(이메일 입력)로 이동
                val intent = Intent(this, RegisterInputEmailActivity::class.java)
                intent.putExtra("password", pw)
                startActivity(intent)
            }
        }
    }

    // 반드시 onCreate 바깥(클래스 내부)에 선언!
    private fun isValid(): Boolean {
        val pw = edtPassword.text.toString()
        val pw2 = edtPasswordConfirm.text.toString()
        return pw.isNotBlank() && pw2.isNotBlank()
    }

    private fun updateButtonState() {
        val enable = isValid()
        btnNext.isEnabled = enable
        if (enable) {
            btnNext.setBackgroundColor(Color.parseColor("#73605A"))
            btnNext.setTextColor(Color.parseColor("#F4EED4"))
            btnNext.strokeColor = ColorStateList.valueOf(Color.parseColor("#FF4D403C"))
        } else {
            btnNext.setBackgroundColor(Color.parseColor("#F4EED4"))
            btnNext.setTextColor(Color.parseColor("#8073605A"))
            btnNext.strokeColor = ColorStateList.valueOf(Color.parseColor("#804D403C"))
        }
    }
}
