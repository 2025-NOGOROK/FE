package com.example.nogorok.features.auth.survey.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.nogorok.databinding.FragmentSurveyStep7Binding
import com.example.nogorok.features.auth.survey.SurveyActivity
import com.example.nogorok.features.auth.survey.SurveyViewModel

class SurveyStep7Fragment : Fragment() {

    private var _binding: FragmentSurveyStep7Binding? = null
    private val binding get() = _binding!!

    private val viewModel: SurveyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSurveyStep7Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateNextButtonState()
                updateEditTextColor(binding.edtMethod1)
                updateEditTextColor(binding.edtMethod2)
                updateEditTextColor(binding.edtMethod3)
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.edtMethod1.addTextChangedListener(watcher)
        binding.edtMethod2.addTextChangedListener(watcher)
        binding.edtMethod3.addTextChangedListener(watcher)

        binding.btnNext.setOnClickListener {
            val method1 = binding.edtMethod1.text.toString()
            val method2 = binding.edtMethod2.text.toString()
            val method3 = binding.edtMethod3.text.toString()

            viewModel.stressReliefMethods.value = listOf(method1, method2, method3)

            (activity as? SurveyActivity)?.navigateToNext("Step7")
        }
    }

    private fun updateNextButtonState() {
        val allFilled = binding.edtMethod1.text.isNotBlank() &&
                binding.edtMethod2.text.isNotBlank() &&
                binding.edtMethod3.text.isNotBlank()
        binding.btnNext.isEnabled = allFilled
    }

    private fun updateEditTextColor(editText: EditText) {
        if (editText.text.isNullOrBlank()) {
            editText.setTextColor(0x80F4EED4.toInt())
        } else {
            editText.setTextColor(0xFFF4EED4.toInt())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}