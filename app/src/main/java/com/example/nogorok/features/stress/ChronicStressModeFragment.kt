package com.example.nogorok.features.stress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.nogorok.R
import com.google.android.material.button.MaterialButton

class ChronicStressModeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 임시 스텁: 레이아웃만 inflate
        return inflater.inflate(R.layout.fragment_chronic_stress_mode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뒤로가기 버튼 기능
        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            requireActivity().onBackPressed()
        }

        // 활성화 버튼 기능 (임시)
        view.findViewById<MaterialButton>(R.id.btnComplete).setOnClickListener {
            // TODO: 만성 스트레스 모드 활성화 로직 연결
        }
    }
}
