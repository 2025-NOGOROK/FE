package com.example.nogorok

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class RegisterInputBirthGenderActivity : AppCompatActivity() {

    private lateinit var etYear: EditText
    private lateinit var etMonth: EditText
    private lateinit var etDay: EditText
    private lateinit var tvMale: TextView
    private lateinit var tvFemale: TextView
    private lateinit var tvNone: TextView
    private lateinit var btnNext: MaterialButton

    // 선택된 성별 값 (null이면 미선택)
    private var selectedGender: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_input_birth_gender)

        etYear = findViewById(R.id.etYear)
        etMonth = findViewById(R.id.etMonth)
        etDay = findViewById(R.id.etDay)
        tvMale = findViewById(R.id.tvMale)
        tvFemale = findViewById(R.id.tvFemale)
        tvNone = findViewById(R.id.tvNone)
        btnNext = findViewById(R.id.btnNext)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        etYear.filters = arrayOf(InputFilter.LengthFilter(4))
        etMonth.filters = arrayOf(InputFilter.LengthFilter(2))
        etDay.filters = arrayOf(InputFilter.LengthFilter(2))

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { updateButtonState() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        etYear.addTextChangedListener(watcher)
        etMonth.addTextChangedListener(watcher)
        etDay.addTextChangedListener(watcher)

        val genderViews = listOf(tvMale, tvFemale, tvNone)
        genderViews.forEach { tv ->
            tv.setOnClickListener {
                // 이미 선택된 상태에서 같은 항목을 다시 누르면 해제
                if (selectedGender == tv.text.toString()) {
                    // 모두 미선택(Flat) 스타일로 초기화
                    genderViews.forEach {
                        it.setBackgroundResource(R.drawable.bg_gender_unselected)
                        it.setTextColor(Color.parseColor("#73605A"))
                    }
                    selectedGender = null
                } else {
                    // 모두 미선택(Flat) 스타일로 초기화
                    genderViews.forEach {
                        it.setBackgroundResource(R.drawable.bg_gender_unselected)
                        it.setTextColor(Color.parseColor("#73605A"))
                    }
                    // 선택된 것만 하이라이트
                    tv.setBackgroundResource(R.drawable.bg_gender_selected)
                    tv.setTextColor(Color.parseColor("#F4EED4"))
                    selectedGender = tv.text.toString()
                }
                updateButtonState()
            }
        }

        btnNext.setOnClickListener {
            val year = etYear.text.toString()
            val month = etMonth.text.toString().padStart(2, '0')
            val day = etDay.text.toString().padStart(2, '0')
            val intent = Intent(this, RegisterInputEmailActivity::class.java)
            intent.putExtra("birth", "$year-$month-$day")
            intent.putExtra("gender", selectedGender)
            startActivity(intent)
        }


        updateButtonState()
    }

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
        val genderOk = selectedGender != null
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