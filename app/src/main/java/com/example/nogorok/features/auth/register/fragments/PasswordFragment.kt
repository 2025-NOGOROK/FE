package com.example.nogorok.features.auth.register.fragments

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.nogorok.R
import com.example.nogorok.features.auth.register.RegisterActivity
import com.example.nogorok.features.auth.register.RegisterViewModel
import com.google.android.material.button.MaterialButton

class PasswordFragment : Fragment() {

    private lateinit var edtPassword: EditText
    private lateinit var edtPasswordConfirm: EditText
    private lateinit var btnNext: MaterialButton
    private lateinit var btnBack: ImageButton
    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_register_input_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[RegisterViewModel::class.java]

        edtPassword = view.findViewById(R.id.edtPassword)
        edtPasswordConfirm = view.findViewById(R.id.edtPasswordConfirm)
        btnNext = view.findViewById(R.id.btnNext)
        btnBack = view.findViewById(R.id.btnBack)

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { updateButtonState() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        edtPassword.addTextChangedListener(watcher)
        edtPasswordConfirm.addTextChangedListener(watcher)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        updateButtonState()

        btnNext.setOnClickListener {
            val pw = edtPassword.text.toString()
            val pw2 = edtPasswordConfirm.text.toString()

            if (pw != pw2) {
                AlertDialog.Builder(requireContext())
                    .setMessage("비밀번호가 일치하지 않습니다.")
                    .setPositiveButton("확인", null)
                    .show()
            } else {
                // ✅ ViewModel에 저장
                viewModel.password = pw
                viewModel.confirmPassword = pw2

                // ✅ RegisterActivity를 통해 다음 단계로 이동
                (activity as? RegisterActivity)?.navigateToNext("Password")
            }
        }
    }

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
