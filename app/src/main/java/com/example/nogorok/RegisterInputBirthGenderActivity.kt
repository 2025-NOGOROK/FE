package com.example.nogorok

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class RegisterInputBirthGenderActivity : AppCompatActivity() {

    // 뷰 변수 선언
    private lateinit var etYear: EditText
    private lateinit var etMonth: EditText
    private lateinit var etDay: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var rbMale: RadioButton
    private lateinit var rbFemale: RadioButton
    private lateinit var rbNone: RadioButton
    private lateinit var btnNext: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_input_birth_gender)

        // findViewById로 뷰 연결
        etYear = findViewById(R.id.etYear)
        etMonth = findViewById(R.id.etMonth)
        etDay = findViewById(R.id.etDay)
        rgGender = findViewById(R.id.rgGender)
        rbMale = findViewById(R.id.rbMale)
        rbFemale = findViewById(R.id.rbFemale)
        rbNone = findViewById(R.id.rbNone)
        btnNext = findViewById(R.id.btnNext)

        // 뒤로가기 버튼
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        // 입력값 제한 (연도 4자리, 월/일 2자리)
        etYear.filters = arrayOf(InputFilter.LengthFilter(4))
        etMonth.filters = arrayOf(InputFilter.LengthFilter(2))
        etDay.filters = arrayOf(InputFilter.LengthFilter(2))

        // 입력값, 성별 선택 감지해서 버튼 활성/비활성
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { updateButtonState() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        etYear.addTextChangedListener(watcher)
        etMonth.addTextChangedListener(watcher)
        etDay.addTextChangedListener(watcher)
        rgGender.setOnCheckedChangeListener { _, _ -> updateButtonState() }

        // 다음 버튼 클릭 시 다음 단계로 이동
        btnNext.setOnClickListener {
            val year = etYear.text.toString()
            val month = etMonth.text.toString().padStart(2, '0')
            val day = etDay.text.toString().padStart(2, '0')
            val gender = when (rgGender.checkedRadioButtonId) {
                R.id.rbMale -> "남"
                R.id.rbFemale -> "여"
                R.id.rbNone -> "선택 안 함"
                else -> ""
            }
            val intent = Intent(this, RegisterInputPasswordActivity::class.java)
            intent.putExtra("birth", "$year-$month-$day")
            intent.putExtra("gender", gender)
            startActivity(intent)
        }

        // 처음 진입 시 버튼 비활성화
        updateButtonState()
    }

    // 함수는 클래스 내부, onCreate 바깥에 선언!
    private fun isValidDate(): Boolean {
        val year = etYear.text.toString()
        val month = etMonth.text.toString()
        val day = etDay.text.toString()
        if (year.length != 4 || month.length !in 1..2 || day.length !in 1..2) return false
        val y = year.toIntOrNull() ?: return false
        val m = month.toIntOrNull() ?: return false
        val d = day.toIntOrNull() ?: return false
        if (m !in 1..12) return false
        if (d !in 1..31) return false
        return true
    }

    private fun updateButtonState() {
        val dateOk = isValidDate()
        val genderOk = rgGender.checkedRadioButtonId != -1
        val enable = dateOk && genderOk

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
