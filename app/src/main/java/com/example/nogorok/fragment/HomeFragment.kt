package com.example.nogorok.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.nogorok.R

class HomeFragment : Fragment() {

    private lateinit var heartRateText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        heartRateText = view.findViewById(R.id.heart_rate_text)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 단순 텍스트 표시
        heartRateText.text = "심박수 데이터를 불러오려면 권한이 필요합니다."
    }
}