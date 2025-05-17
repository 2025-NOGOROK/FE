package com.example.nogorok.features.auth.survey.fragments

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
            val request = SurveyRequest(
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
                        Toast.makeText(requireContext(), "서버 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                } catch (e: HttpException) {
                    Toast.makeText(requireContext(), "HTTP 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "알 수 없는 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
