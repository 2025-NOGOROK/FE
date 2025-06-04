package com.example.nogorok.features.auth.register.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.nogorok.R
import com.example.nogorok.features.auth.register.RegisterViewModel
import com.example.nogorok.features.auth.survey.SurveyActivity
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.SignUpRequest
import com.example.nogorok.network.dto.SignUpResponse
import com.example.nogorok.utils.TokenManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CompleteFragment : Fragment() {

    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_register_input_complete, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(requireActivity())[RegisterViewModel::class.java]

        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val btnSurvey = view.findViewById<MaterialButton>(R.id.btnSurvey)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnSurvey.setOnClickListener {
            sendSignUpRequest()
        }
    }

    private fun sendSignUpRequest() {
        val notificationAgreed = viewModel.notificationAgreed
        val deviceToken = viewModel.deviceToken

        if (notificationAgreed && deviceToken.isNullOrBlank()) {
            Toast.makeText(requireContext(), "푸시 알림을 동의했으나 토큰이 없습니다", Toast.LENGTH_SHORT).show()
            return
        }

        val request = SignUpRequest(
            name = viewModel.name ?: "",
            birth = viewModel.birth ?: "",
            gender = viewModel.gender ?: "",
            email = viewModel.email ?: "",
            password = viewModel.password ?: "",
            confirmPassword = viewModel.confirmPassword ?: "",
            pushNotificationAgreed = notificationAgreed,
            deviceToken = deviceToken ?: "",
            termsOfServiceAgreed = viewModel.agreedService,
            privacyPolicyAgreed = viewModel.agreedPrivacy,
            healthInfoPolicyAgreed = viewModel.agreedHealth,
            locationPolicyAgreed = viewModel.agreedLocation
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.authApi.signUp(request)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val body = response.body() as? SignUpResponse
                        val accessToken = body?.data?.accessToken

                        accessToken?.let {
                            // ✅ accessToken 저장 및 등록
                            TokenManager.saveAccessToken(requireContext(), it)
                            TokenManager.saveEmail(requireContext(), viewModel.email ?: "")
                            RetrofitClient.setAccessToken(it)

                            // ✅ accessToken 등록 후 FCM 토큰 전송
                            val token = viewModel.deviceToken
                            if (!token.isNullOrBlank()) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        RetrofitClient.fcmApi.registerFcmToken(token)
                                        Log.d("CompleteFragment", "FCM 토큰 서버 전송 성공")
                                    } catch (e: Exception) {
                                        Log.e("CompleteFragment", "FCM 토큰 전송 실패: ${e.message}")
                                    }
                                }
                            }
                        }

                        Toast.makeText(requireContext(), "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), SurveyActivity::class.java)
                        intent.putExtra("mode", "register")
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        Toast.makeText(requireContext(), "회원가입 실패: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("CompleteFragment", "회원가입 오류", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
