package com.example.nogorok.features.rest.longrest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nogorok.R
import com.google.android.material.button.MaterialButton

class ScenarioFragment : Fragment() {

    companion object {
        private const val ARG_SCENARIO_NUMBER = "scenario_number"
        fun newInstance(number: Int): ScenarioFragment {
            val fragment = ScenarioFragment()
            val args = Bundle()
            args.putInt(ARG_SCENARIO_NUMBER, number)
            fragment.arguments = args
            return fragment
        }
    }

    private var scenarioNumber: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            scenarioNumber = it.getInt(ARG_SCENARIO_NUMBER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_scenario, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.itemRecyclerView)
        val selectButton = view.findViewById<MaterialButton>(R.id.selectButton)

        recyclerView.layoutManager = LinearLayoutManager(context)

        val restItems = LongRestViewModel().getScenario(scenarioNumber)
        recyclerView.adapter = RestItemAdapter(restItems)

        // 선택 버튼 동작 (예: 로그 출력)
        selectButton.setOnClickListener {
            // 예시: 선택한 시나리오 번호 출력
            println("시나리오 $scenarioNumber 선택됨")
        }

        return view
    }
}
