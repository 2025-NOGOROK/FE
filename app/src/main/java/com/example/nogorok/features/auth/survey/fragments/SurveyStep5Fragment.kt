package com.example.nogorok.features.auth.survey.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.nogorok.databinding.FragmentSurveyStep5Binding
import com.example.nogorok.features.auth.survey.SurveyActivity
import com.example.nogorok.features.auth.survey.SurveyViewModel

class SurveyStep5Fragment : Fragment() {

    private var _binding: FragmentSurveyStep5Binding? = null
    private val binding get() = _binding!!

    private val viewModel: SurveyViewModel by activityViewModels()

    private var selectedIndex: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSurveyStep5Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val btnList = listOf(
            binding.btnStress1,
            binding.btnStress2,
            binding.btnStress3,
            binding.btnStress4,
            binding.btnStress5
        )

        btnList.forEachIndexed { idx, btn ->
            btn.setOnClickListener {
                if (selectedIndex == idx) {
                    btn.isSelected = false
                    selectedIndex = -1
                    binding.btnNext.isEnabled = false
                } else {
                    btnList.forEach { it.isSelected = false }
                    btn.isSelected = true
                    selectedIndex = idx
                    binding.btnNext.isEnabled = true
                }
            }
        }

        binding.btnNext.setOnClickListener {
            if (selectedIndex != -1) {
                viewModel.stressReaction.value = selectedIndex.toString()
                (activity as? SurveyActivity)?.navigateToNext("Step5")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
