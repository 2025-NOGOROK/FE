package com.example.nogorok.features.auth.register.fragments

import android.content.res.ColorStateList
import androidx.lifecycle.ViewModelProvider
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.nogorok.R
import com.google.android.material.button.MaterialButton
import com.example.nogorok.features.auth.register.RegisterActivity
import com.example.nogorok.features.auth.register.RegisterViewModel

class BirthGenderFragment : Fragment() {

    private lateinit var etYear: EditText
    private lateinit var etMonth: EditText
    private lateinit var etDay: EditText
    private lateinit var tvMale: TextView
    private lateinit var tvFemale: TextView
    private lateinit var tvNone: TextView
    private lateinit var btnNext: MaterialButton
    private lateinit var btnBack: ImageButton

    private var selectedGender: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_register_input_birth_gender, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        etYear = view.findViewById(R.id.etYear)
        etMonth = view.findViewById(R.id.etMonth)
        etDay = view.findViewById(R.id.etDay)
        tvMale = view.findViewById(R.id.tvMale)
        tvFemale = view.findViewById(R.id.tvFemale)
        tvNone = view.findViewById(R.id.tvNone)
        btnNext = view.findViewById(R.id.btnNext)
        btnBack = view.findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        etYear.filters = arrayOf(InputFilter.LengthFilter(4))
        etMonth.filters = arrayOf(InputFilter.LengthFilter(2))
        etDay.filters = arrayOf(InputFilter.LengthFilter(2))

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = updateButtonState()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        etYear.addTextChangedListener(watcher)
        etMonth.addTextChangedListener(watcher)
        etDay.addTextChangedListener(watcher)

        val genderViews = listOf(tvMale, tvFemale, tvNone)
        genderViews.forEach { tv ->
            tv.setOnClickListener {
                // 클릭한 뷰에 따라 selectedGender 설정
                selectedGender = when (tv.id) {
                    R.id.tvMale -> "M"
                    R.id.tvFemale -> "F"
                    R.id.tvNone -> null
                    else -> null
                }

                // 모든 성별 버튼 초기화
                genderViews.forEach {
                    it.setBackgroundResource(R.drawable.bg_gender_unselected)
                    it.setTextColor(Color.parseColor("#73605A"))
                }

                // 선택한 경우만 스타일 적용
                selectedGender?.let {
                    tv.setBackgroundResource(R.drawable.bg_gender_selected)
                    tv.setTextColor(Color.parseColor("#F4EED4"))
                }

                updateButtonState()
            }
        }



        btnNext.setOnClickListener {
            val year = etYear.text.toString()
            val month = etMonth.text.toString().padStart(2, '0')
            val day = etDay.text.toString().padStart(2, '0')
            val birth = "$year-$month-$day"

            // ✅ RegisterViewModel에 저장
            val viewModel = ViewModelProvider(requireActivity())[RegisterViewModel::class.java]
            viewModel.birth = birth
            viewModel.gender = selectedGender

            // 다음 화면으로 이동
            (activity as? RegisterActivity)?.navigateToNext("BirthGender")
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
