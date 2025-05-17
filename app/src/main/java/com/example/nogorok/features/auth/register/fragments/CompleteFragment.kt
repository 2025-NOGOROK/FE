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
import androidx.lifecycle.lifecycleScope
import com.example.nogorok.R
import com.example.nogorok.features.auth.register.RegisterViewModel
import com.example.nogorok.features.auth.survey.SurveyActivity
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.api.FcmApi
import com.example.nogorok.network.dto.FcmTokenRequest
import com.example.nogorok.network.dto.SignUpRequest
import com.example.nogorok.network.dto.SignUpResponse
import com.example.nogorok.utils.TokenManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
                            TokenManager.saveAccessToken(it)
                            TokenManager.saveEmail(requireContext(), viewModel.email ?: "")
                            registerFcmToken(it, deviceToken ?: "")
                        }

                        Toast.makeText(requireContext(), "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(requireContext(), SurveyActivity::class.java))
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

    private fun registerFcmToken(accessToken: String, fcmToken: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://3.35.81.229:8080/")
            .client(
                OkHttpClient.Builder().addInterceptor(Interceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $accessToken")
                        .build()
                    chain.proceed(request)
                }).build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val fcmApi = retrofit.create(FcmApi::class.java)

        lifecycleScope.launch {
            try {
                val response = fcmApi.sendFcm(FcmTokenRequest(fcmToken))
                if (response.isSuccessful) {
                    Log.d("CompleteFragment", "FCM 토큰 등록 성공")
                } else {
                    Log.e("CompleteFragment", "FCM 등록 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("CompleteFragment", "FCM 등록 오류: ${e.message}")
            }
        }
    }
}
