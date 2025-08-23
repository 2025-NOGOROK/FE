package com.example.nogorok.features.auth.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.nogorok.MainActivity
import com.example.nogorok.databinding.FragmentLoginBinding
import com.example.nogorok.features.auth.forgotpassword.FindPasswordEmailActivity
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.SignInRequest
import com.example.nogorok.network.util.apiError   // ★ 공통 에러 파서 사용
import com.example.nogorok.utils.TokenManager
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
            startActivity(Intent(requireContext(), FindPasswordEmailActivity::class.java))
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.edtEmail.addTextChangedListener(createValidationWatcher(binding.emailError))
        binding.edtPassword.addTextChangedListener(createValidationWatcher(binding.passwordError))

        binding.btnLogin.setOnClickListener {
            isValidationActivated = true

            val email = binding.edtEmail.text?.toString()?.trim() ?: ""
            val password = binding.edtPassword.text?.toString()?.trim() ?: ""

            if (validateInputs(email, password)) {
                lifecycleScope.launch {
                    try {
                        val request = SignInRequest(email, password)
                        val response = RetrofitClient.authApi.signIn(request)

                        if (response.isSuccessful) {
                            val result = response.body()
                            val accessToken = result?.data?.accessToken.orEmpty()

                            val appContext = requireContext().applicationContext
                            TokenManager.saveAccessToken(appContext, accessToken)
                            RetrofitClient.setAccessToken(accessToken) // 백업용(옵션)

                            Toast.makeText(requireContext(), "로그인 성공", Toast.LENGTH_SHORT).show()

                            startActivity(Intent(requireContext(), MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            })
                        } else {
                            // ★ 401 + GOOGLE_RELINK_REQUIRED 처리
                            val err = response.apiError()
                            if (response.code() == 401 &&
                                err?.error == "GOOGLE_RELINK_REQUIRED" &&
                                !err.authUrl.isNullOrBlank()
                            ) {
                                AlertDialog.Builder(requireContext())
                                    .setTitle("구글 권한 만료")
                                    .setMessage("구글 권한이 만료되었습니다. 다시 연결해 주세요.")
                                    .setNegativeButton("취소", null)
                                    .setPositiveButton("연결하기") { _, _ ->
                                        startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(err.authUrl)
                                            )
                                        )
                                        // 동의 완료 → 서버 /auth/google/callback → intent://oauth2callback?... 로 앱 복귀
                                    }
                                    .show()
                            } else {
                                val msg = err?.message ?: response.errorBody()?.string() ?: "알 수 없는 오류"
                                Toast.makeText(requireContext(), "로그인 실패: $msg", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "서버 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var valid = true
        if (email.isBlank()) {
            binding.emailError.visibility = View.VISIBLE
            valid = false
        } else binding.emailError.visibility = View.GONE

        if (password.isBlank()) {
            binding.passwordError.visibility = View.VISIBLE
            valid = false
        } else binding.passwordError.visibility = View.GONE

        return valid
    }

    private fun createValidationWatcher(target: View): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isValidationActivated) {
                    target.visibility = if (s.isNullOrBlank()) View.VISIBLE else View.GONE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
