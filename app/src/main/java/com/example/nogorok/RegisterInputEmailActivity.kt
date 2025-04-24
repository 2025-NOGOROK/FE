package com.example.nogorok

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class RegisterInputEmailActivity : AppCompatActivity() {
    // 예시: 이미 가입된 이메일 리스트(실제로는 서버에서 체크)
    private val registeredEmails = listOf("hansongyi@gmail.com", "test@nogorok.com")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_input_email)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)

        // 이메일 입력 감지해서 버튼 활성화
        edtEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                val enable = email.isNotBlank()
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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 다음 버튼 클릭 시 이메일 형식/중복 체크
        btnNext.setOnClickListener {
            val email = edtEmail.text.toString()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                AlertDialog.Builder(this)
                    .setMessage("이메일 양식을 확인해주세요.")
                    .setPositiveButton("확인", null)
                    .show()
            } else if (registeredEmails.contains(email)) {
                AlertDialog.Builder(this)
                    .setMessage("이미 가입된 이메일입니다.")
                    .setPositiveButton("확인", null)
                    .show()
            } else {
                // 다음 단계(비밀번호 입력)로 이동
                val intent = Intent(this, RegisterInputPasswordActivity::class.java)
                intent.putExtra("email", email)
                // 이전 화면에서 받은 데이터도 함께 전달
                intent.putExtras(intentFromPrevious())
                startActivity(intent)
            }
        }
    }

    // 이전 화면에서 받은 데이터도 함께 전달 (생년월일, 성별 등)
    private fun intentFromPrevious(): Bundle {
        return intent.extras ?: Bundle()
    }
}
