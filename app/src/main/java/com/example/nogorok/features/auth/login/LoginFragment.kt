package com.example.nogorok.features.auth.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.nogorok.MainActivity
import com.example.nogorok.databinding.FragmentLoginBinding
import com.example.nogorok.features.auth.forgotpassword.FindPasswordEmailActivity

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var isValidationActivated = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // "비밀번호 찾기" 버튼
        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(requireContext(), FindPasswordEmailActivity::class.java)
            startActivity(intent)
        }

        // 뒤로가기 버튼
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // 이메일 입력 감지
        binding.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isValidationActivated) {
                    binding.emailError.visibility =
                        if (s.isNullOrBlank()) View.VISIBLE else View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 비밀번호 입력 감지
        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isValidationActivated) {
                    binding.passwordError.visibility =
                        if (s.isNullOrBlank()) View.VISIBLE else View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 로그인 버튼 클릭
        binding.btnLogin.setOnClickListener {
            isValidationActivated = true

            val email = binding.edtEmail.text?.toString()?.trim() ?: ""
            val password = binding.edtPassword.text?.toString()?.trim() ?: ""

            var isValid = true

            if (email.isEmpty()) {
                binding.emailError.visibility = View.VISIBLE
                isValid = false
            } else {
                binding.emailError.visibility = View.GONE
            }

            if (password.isEmpty()) {
                binding.passwordError.visibility = View.VISIBLE
                isValid = false
            } else {
                binding.passwordError.visibility = View.GONE
            }

            if (isValid) {
                Toast.makeText(requireContext(), "로그인 시도: $email", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
