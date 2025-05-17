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
        val request = SignUpRequest(
            name = viewModel.name ?: "",
            birth = viewModel.birth ?: "",
            gender = viewModel.gender ?: "",
            email = viewModel.email ?: "",
            password = viewModel.password ?: "",
            confirmPassword = viewModel.password ?: "",
            pushNotificationAgreed = viewModel.notificationAgreed,
            deviceToken = viewModel.deviceToken ?: "",
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
                        Toast.makeText(requireContext(), "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), SurveyActivity::class.java)
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
