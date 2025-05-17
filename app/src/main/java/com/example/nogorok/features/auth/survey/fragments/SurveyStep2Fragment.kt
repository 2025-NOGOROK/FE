package com.example.nogorok.features.auth.survey.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.nogorok.databinding.FragmentSurveyStep2Binding
import com.example.nogorok.features.auth.survey.SurveyActivity
import com.example.nogorok.features.auth.survey.SurveyViewModel

class SurveyStep2Fragment : Fragment() {

    private var _binding: FragmentSurveyStep2Binding? = null
    private val binding get() = _binding!!
    private val viewModel: SurveyViewModel by activityViewModels()

    private var selectedOption: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSurveyStep2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnLoose.setOnClickListener {
            selectedOption = if (selectedOption == 1) 0 else 1
            updateSelection()
        }

        binding.btnTight.setOnClickListener {
            selectedOption = if (selectedOption == 2) 0 else 2
            updateSelection()
        }

        binding.btnNext.setOnClickListener {
            viewModel.suddenChangePreferred.value = (selectedOption == 1)
            (activity as? SurveyActivity)?.navigateToNext("Step2")
        }
    }

    private fun updateSelection() {
        binding.btnLoose.isSelected = (selectedOption == 1)
        binding.btnTight.isSelected = (selectedOption == 2)
        binding.btnNext.isEnabled = (selectedOption != 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
