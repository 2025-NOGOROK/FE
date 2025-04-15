package com.example.nogorok.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.nogorok.R
import com.example.nogorok.model.HeartRateViewModel

class HomeFragment : Fragment() {

    private lateinit var viewModel: HeartRateViewModel
    private lateinit var heartRateText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        heartRateText = view.findViewById(R.id.heart_rate_text)

        // ViewModel 연결
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        ).get(HeartRateViewModel::class.java)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Samsung Health 연결 시작
        viewModel.initHealthStore()

        // 2. 심박수 LiveData 관찰
        viewModel.heartRates.observe(viewLifecycleOwner) { rates ->
            val average = if (rates.isNotEmpty()) "%.1f".format(rates.average()) else "N/A"
            heartRateText.text = "심박수 평균: $average bpm"
        }

        // 3. 권한 필요 시 ViewModel이 알려주면 요청 실행
        viewModel.permissionRequired.observe(viewLifecycleOwner) { needed ->
            if (needed) {
                viewModel.requestPermissions(requireActivity())
            }
        }
    }
}
