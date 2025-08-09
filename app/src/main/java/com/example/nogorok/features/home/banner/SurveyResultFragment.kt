package com.example.nogorok.features.survey

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nogorok.R
import com.example.nogorok.databinding.FragmentSurveyResultBinding

/**
 * 결과 타입에 따라 UI가 달라지는 배너 설문 결과 화면
 *
 * arguments:
 *  - "result": String = "STABLE" | "CAUTION" | "SERIOUS"
 */
class SurveyResultFragment : Fragment() {

    private var _binding: FragmentSurveyResultBinding? = null
    private val binding get() = _binding!!

    enum class ResultType { STABLE, CAUTION, SERIOUS }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSurveyResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resultArg = arguments?.getString("result") ?: "STABLE"
        val type = runCatching { ResultType.valueOf(resultArg) }.getOrElse { ResultType.STABLE }

        renderUi(type)

        // 뒤로가기: 설문 페이지로 복귀 (단순 popBackStack)
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // 하단 버튼 액션
        binding.btnPrimary.setOnClickListener {
            when (type) {
                ResultType.SERIOUS -> {
                    // 임시: 캘린더(일정) 화면으로 이동
                    // 프로젝트의 nav_graph에 존재하는 fragment id 사용 (예: scheduleFragment)
                    findNavController().navigate(R.id.scheduleFragment)
                }
                ResultType.STABLE, ResultType.CAUTION -> {
                    // 홈으로 이동
                    findNavController().navigate(R.id.homeFragment)
                }
            }
        }
    }

    private fun renderUi(type: ResultType) {
        when (type) {
            ResultType.STABLE -> {
                binding.imgStatus.setImageResource(R.drawable.stable)
                binding.txtTitle.text = coloredTitle(
                    full = "스트레스가 안정 범위에 있어요.",
                    highlight = "안정 범위",
                    color = 0xFF3E9D5C.toInt()
                )
                binding.txtSubtitle.text = "현재 신체적·정서적 긴장 반응이 낮은 수준으로 나타\n났어요. 일상에서 스트레스를 잘 조절하고 있는 상태\n로 보입니다. 안정된 컨디션을 유지하기 위해서는 주\n기적인 휴식과 감정 체크를 권장드려요."
                binding.btnPrimary.text = "홈으로 돌아가기"
            }
            ResultType.CAUTION -> {
                binding.imgStatus.setImageResource(R.drawable.caution)
                binding.txtTitle.text = coloredTitle(
                    full = "스트레스가 주의 단계에 있어요.",
                    highlight = "주의 단계",
                    color = 0xFFB95E18.toInt()
                )
                binding.txtSubtitle.text = "일상적인 자극에 대한 반응이 예민해지고 있\n을 수 있어요. 피로, 수면 변화, 감정 기복 등의 신호가 나타날\n가능성이 있습니다. 스트레스 관리가 필요한 시점이에\n요. 홈에서 제안하는 맞춤 관리법을 참고해보세요."
                binding.btnPrimary.text = "홈으로 돌아가기"
            }
            ResultType.SERIOUS -> {
                binding.imgStatus.setImageResource(R.drawable.serious)
                binding.txtTitle.text = coloredTitle(
                    full = "스트레스 수준이 매우 높아요.",
                    highlight = "매우 높아요",
                    color = 0xFFEA1D1D.toInt()
                )
                binding.txtSubtitle.text = "감정 조절, 수면, 집중력 등에 이미 영향을 주고 있을\n가능성이 높습니다. 현재 상태는 고위험군에 해당하\n며, 즉각적인 스트레스 완화 조치가 필요합니다. 지금\n바로 회복을 시작해보세요."
                binding.btnPrimary.text = "짧은 쉼표 생성하기"
            }
        }
    }

    private fun coloredTitle(full: String, highlight: String, color: Int): CharSequence {
        val s = SpannableString(full)
        val start = full.indexOf(highlight)
        if (start >= 0) {
            s.setSpan(
                ForegroundColorSpan(color),
                start, start + highlight.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return s
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(type: ResultType) = SurveyResultFragment().apply {
            arguments = bundleOf("result" to type.name)
        }
    }
}
