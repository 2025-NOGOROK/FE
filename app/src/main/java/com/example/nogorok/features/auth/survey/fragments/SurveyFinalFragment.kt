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

        // 👉 안내 문구와 버튼 텍스트 변경
        if (viewModel.mode == "edit") {
            binding.tvDesc.text = "기존 설문 내용을 수정하고\n업데이트할 수 있어요."
            binding.btnNext.text = "완료"
        } else {
            binding.tvDesc.text = "고생하셨어요!\n이제 당신의 루틴을 완성할\n두 가지 설정만 남았어요."
            binding.btnNext.text = "다음"
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnNext.setOnClickListener {
            if (viewModel.hasStressRelief.value == true &&
                (viewModel.stressReliefMethods.value.isNullOrEmpty())) {
                Toast.makeText(requireContext(), "스트레스 해소 방법을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userEmail = TokenManager.getEmail(requireContext()) ?: ""

            val request = SurveyRequest(
                email = userEmail,
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
                    val response = if (viewModel.mode == "edit") {
                        RetrofitClient.surveyApi.updateSurvey(request)
                    } else {
                        RetrofitClient.surveyApi.submitSurvey(request)
                    }

                    if (response.isSuccessful) {
                        if (viewModel.mode == "edit") {
                            Toast.makeText(requireContext(), "설문 정보가 수정되었습니다", Toast.LENGTH_SHORT).show()
                            requireActivity().finish()
                        } else {
                            val intent = Intent(requireContext(), HealthMainActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }
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
