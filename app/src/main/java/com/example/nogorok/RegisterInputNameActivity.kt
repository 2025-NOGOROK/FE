package com.example.nogorok

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

class RegisterInputNameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_input_name)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val edtName = findViewById<EditText>(R.id.edtName)
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)

        // 입력 감지해서 버튼 활성/비활성 및 스타일 변경
        edtName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val isNotEmpty = !s.isNullOrBlank()
                btnNext.isEnabled = isNotEmpty
                if (isNotEmpty) {
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

        // 다음 버튼 클릭 시 다음 단계로 이동
        btnNext.setOnClickListener {
            val intent = Intent(this, RegisterInputBirthGenderActivity::class.java)
            intent.putExtra("nickname", edtName.text.toString())
            startActivity(intent)
        }
    }
}
