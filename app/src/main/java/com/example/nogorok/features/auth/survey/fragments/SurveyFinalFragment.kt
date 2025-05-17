package com.example.nogorok.features.auth.survey.fragments

import com.example.nogorok.utils.TokenManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.nogorok.databinding.FragmentSurveyFinalBinding
import com.example.nogorok.features.auth.survey.SurveyViewModel
import com.example.nogorok.features.connect.health.HealthMainActivity
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.SurveyRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

// 생략된 import는 그대로 유지

class SurveyFinalFragment : Fragment() {
    private var _binding: FragmentSurveyFinalBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SurveyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSurveyFinalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnNext.setOnClickListener {
            // ✅ 유효성 체크
            if (viewModel.hasStressRelief.value == true &&
                (viewModel.stressReliefMethods.value.isNullOrEmpty())) {
                Toast.makeText(requireContext(), "스트레스 해소 방법을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userEmail = TokenManager.getEmail(requireContext()) // 또는 ViewModel에서 받아올 수도 있음

            val request = SurveyRequest(
                email = userEmail ?: "", // ❗ null 방지를 위해 기본값 처리
                scheduleType = viewModel.scheduleType.value ?: "",
                suddenChangePreferred = viewModel.suddenChangePreferred.value ?: false,
                chronotype = viewModel.chronotype.value ?: "",
                preferAlone = viewModel.preferAlone.value ?: "",
                stressReaction = viewModel.stressReaction.value ?: "",
                hasStressRelief = viewModel.hasStressRelief.value ?: false,
                stressReliefMethods = viewModel.stressReliefMethods.value ?: emptyList()
            )


            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.surveyApi.submitSurvey(request)
                    if (response.isSuccessful) {
                        val intent = Intent(requireContext(), HealthMainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        Toast.makeText(requireContext(), "서버 오류 발생: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "오류: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
