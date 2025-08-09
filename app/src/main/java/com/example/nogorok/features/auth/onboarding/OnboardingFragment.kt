package com.example.nogorok.features.auth.onboarding

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.nogorok.R
import com.example.nogorok.features.auth.login.SignupLoginActivity
import com.google.android.material.button.MaterialButton
import android.widget.ImageView
import android.widget.TextView

class OnboardingFragment : Fragment() {

    companion object {
        private const val ARG_TITLE     = "arg_title"
        private const val ARG_SUBTITLE  = "arg_subtitle"
        private const val ARG_IMAGE_RES = "arg_image_res"
        private const val ARG_IS_LAST   = "arg_is_last"
        private const val ARG_POSITION  = "arg_position"

        fun newInstance(
            title: String,
            subtitle: String,
            imageResId: Int,
            isLastPage: Boolean,
            position: Int
        ): OnboardingFragment {
            return OnboardingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_SUBTITLE, subtitle)
                    putInt(ARG_IMAGE_RES, imageResId)
                    putBoolean(ARG_IS_LAST, isLastPage)
                    putInt(ARG_POSITION, position)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_onboarding, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) 전달된 인자 꺼내기
        val args       = requireArguments()
        val title      = args.getString(ARG_TITLE, "")
        val subtitle   = args.getString(ARG_SUBTITLE, "")
        val imageRes   = args.getInt(ARG_IMAGE_RES, 0)
        val isLastPage = args.getBoolean(ARG_IS_LAST, false)
        val position   = args.getInt(ARG_POSITION, 0)

        // 2) 뷰 바인딩
        val tvTitle    = view.findViewById<TextView>(R.id.tvTitle)
        val tvSubtitle = view.findViewById<TextView>(R.id.tvSubtitle)
        val ivImage    = view.findViewById<ImageView>(R.id.ivImage)
        val btnNext    = view.findViewById<MaterialButton>(R.id.btnNext)
        val commaIcons = listOf(
            view.findViewById<ImageView>(R.id.ivComma0),
            view.findViewById<ImageView>(R.id.ivComma1),
            view.findViewById<ImageView>(R.id.ivComma2),
            view.findViewById<ImageView>(R.id.ivComma3),
            view.findViewById<ImageView>(R.id.ivComma4),
            view.findViewById<ImageView>(R.id.ivComma5)
        )

        // 3) 콘텐츠 세팅
        tvTitle.text    = title
        tvSubtitle.text = subtitle
        ivImage.setImageResource(imageRes)

        // 4) 콤마 인디케이터 토글
        val darkIndex = when (position) {
            0    -> 0
            1    -> 1
            2    -> 2
            3    -> 3
            4    -> 4
            5    -> 5
            else -> 0
        }
        commaIcons.forEachIndexed { idx, iv ->
            iv.setImageResource(
                if (idx == darkIndex) R.drawable.onboarding_comma1
                else                   R.drawable.onboarding_comma2
            )
        }

        // 5) 버튼 텍스트 투명도 & 외곽선 색상 고정
        // 외곽선 색상: #4D403C 50% alpha -> 0x804D403C
        val strokeColor50 = Color.parseColor("#804D403C")
        btnNext.strokeColor = ColorStateList.valueOf(strokeColor50)

        // 텍스트 색상: 기본 #73605A, 50% alpha for non-last
        val textColorFull = Color.parseColor("#73605A")
        val textColorHalf = ColorUtils.setAlphaComponent(textColorFull, 128)
        btnNext.setTextColor(if (isLastPage) textColorFull else textColorHalf)

        // 버튼 텍스트 변경
        btnNext.text = if (isLastPage) "시작하기" else "다음"

        // 6) 버튼 클릭 처리
        btnNext.setOnClickListener {
            if (isLastPage) {
                startActivity(Intent(requireContext(), SignupLoginActivity::class.java))
                requireActivity().finish()
            } else {
                requireActivity()
                    .findViewById<ViewPager2>(R.id.onboardingViewPager)
                    .currentItem = position + 1
            }
        }
    }
}
