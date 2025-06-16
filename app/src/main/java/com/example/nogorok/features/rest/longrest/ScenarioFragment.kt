package com.example.nogorok.features.rest.longrest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nogorok.R
import com.google.android.material.button.MaterialButton
import android.widget.Toast


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

    private var scenarioIndex: Int = 0

    // 🔄 activityViewModels를 사용해 동일 ViewModel 공유
    private val viewModel: LongRestViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // 시나리오 번호는 1부터 시작하므로 index는 -1
            scenarioIndex = it.getInt(ARG_SCENARIO_NUMBER) - 1
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_scenario, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.itemRecyclerView)
        val selectButton = view.findViewById<MaterialButton>(R.id.selectButton)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = RestItemAdapter(emptyList())
        recyclerView.adapter = adapter

        // 🔄 scenarioItems를 관찰하고 해당 인덱스의 데이터만 뿌려줌
        viewModel.scenarioItems.observe(viewLifecycleOwner) { allScenarios ->
            val items = allScenarios.getOrNull(scenarioIndex) ?: emptyList()
            adapter.updateItems(items)
        }

        // 🔄 최초 한 번만 fetch (Activity 단에서 호출해도 됨)
        if (viewModel.scenarioItems.value == null) {
            viewModel.fetchLongRestItems { e ->
                println("데이터 로딩 실패: ${e.message}")
            }
        }

        selectButton.setOnClickListener {
            viewModel.postSelectedScenario(scenarioIndex,
                onSuccess = {
                    Toast.makeText(requireContext(), "시나리오 ${scenarioIndex + 1} 선택 완료!", Toast.LENGTH_SHORT).show()
                },
                onError = {
                    Toast.makeText(requireContext(), "선택 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }


        return view
    }
}
