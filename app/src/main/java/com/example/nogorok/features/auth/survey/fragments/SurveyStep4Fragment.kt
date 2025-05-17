package com.example.nogorok.features.auth.survey.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.nogorok.databinding.FragmentSurveyStep4Binding
import com.example.nogorok.features.auth.survey.SurveyActivity
import com.example.nogorok.features.auth.survey.SurveyViewModel

class SurveyStep4Fragment : Fragment() {

    private var _binding: FragmentSurveyStep4Binding? = null
    private val binding get() = _binding!!

    private val viewModel: SurveyViewModel by activityViewModels()
    private var selectedOption: Int = 0 // 0: 선택 안함, 1: 혼자, 2: 함께

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSurveyStep4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnLoose.setOnClickListener {
            if (selectedOption == 1) {
                selectedOption = 0
                binding.btnLoose.isSelected = false
                binding.btnTight.isSelected = false
            } else {
                selectedOption = 1
                binding.btnLoose.isSelected = true
                binding.btnTight.isSelected = false
            }
            updateNextButtonState()
        }

        binding.btnTight.setOnClickListener {
            if (selectedOption == 2) {
                selectedOption = 0
                binding.btnLoose.isSelected = false
                binding.btnTight.isSelected = false
            } else {
                selectedOption = 2
                binding.btnLoose.isSelected = false
                binding.btnTight.isSelected = true
            }
            updateNextButtonState()
        }

        binding.btnNext.setOnClickListener {
            val preferAlone = when (selectedOption) {
                1 -> "alone"
                2 -> "together"
                else -> ""
            }

            viewModel.preferAlone.value = preferAlone
            (activity as? SurveyActivity)?.navigateToNext("Step4")
        }
    }

    private fun updateNextButtonState() {
        binding.btnNext.isEnabled = selectedOption != 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
