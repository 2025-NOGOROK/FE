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
import androidx.lifecycle.lifecycleScope
import com.example.nogorok.MainActivity
import com.example.nogorok.databinding.FragmentLoginBinding
import com.example.nogorok.features.auth.forgotpassword.FindPasswordEmailActivity
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.SignInRequest
import kotlinx.coroutines.launch

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

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(requireContext(), FindPasswordEmailActivity::class.java)
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isValidationActivated) {
                    binding.emailError.visibility = if (s.isNullOrBlank()) View.VISIBLE else View.GONE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isValidationActivated) {
                    binding.passwordError.visibility = if (s.isNullOrBlank()) View.VISIBLE else View.GONE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

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
                lifecycleScope.launch {
                    try {
                        val request = SignInRequest(email, password)
                        val response = RetrofitClient.authApi.signIn(request)

                        if (response.isSuccessful) {
                            val result = response.body()
                            val accessToken = result?.data?.accessToken ?: ""
                            val refreshToken = result?.data?.refreshToken ?: ""

                            // TODO: accessToken, refreshToken 저장 (SharedPreferences 등)
                            Toast.makeText(requireContext(), "로그인 성공", Toast.LENGTH_SHORT).show()

                            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                        } else {
                            Toast.makeText(requireContext(), "로그인 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "서버 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
