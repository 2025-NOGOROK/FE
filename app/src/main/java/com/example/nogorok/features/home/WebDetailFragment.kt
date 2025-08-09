package com.example.nogorok.web

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nogorok.R
import com.example.nogorok.databinding.FragmentWebDetailBinding

class WebDetailFragment : Fragment(R.layout.fragment_web_detail) {

    private var _binding: FragmentWebDetailBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 뷰 바인딩 초기화
        _binding = FragmentWebDetailBinding.bind(view)

        // 백버튼 클릭 시 이전 화면으로
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
