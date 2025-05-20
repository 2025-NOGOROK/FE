package com.example.nogorok.features.home

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.nogorok.databinding.FragmentHomeBinding
import com.example.nogorok.features.connect.health.utils.HeartRateLocal
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.text.style.ImageSpan
import androidx.core.content.ContextCompat
import com.example.nogorok.R
import com.example.nogorok.utils.CustomTypefaceSpan
import androidx.core.content.res.ResourcesCompat





class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val stress = getLatestStoredStress(requireContext()) ?: 0f

        val stressMessage = SpannableStringBuilder().apply {
            val label = "당신의 최근 스트레스 지수는\n"
            append(label)
            // 🔹 세미볼드 Typeface 불러오기
            val semiBoldTypeface = ResourcesCompat.getFont(requireContext(), R.font.pretendard_semibold)

            semiBoldTypeface?.let {
                // 🔹 세미볼드 폰트 적용
                setSpan(CustomTypefaceSpan(it), 0, label.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

// 🔹 색상 적용
            setSpan(
                ForegroundColorSpan(Color.parseColor("#73605A")),
                0,
                label.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // ❤️ 이미지 삽입
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.love)
            drawable?.setBounds(0, 0, 100,86 )

            val imageSpan = drawable?.let { ImageSpan(it, ImageSpan.ALIGN_BASELINE) }

            val imageStart = length
            append("❤️") // 임시로 추가
            if (imageSpan != null) {
                setSpan(imageSpan, imageStart, imageStart + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            // ✅ 스트레스 수치 텍스트
            val bold = ResourcesCompat.getFont(requireContext(), R.font.pretendard_bold)
            val scoreText = " ${stress.toInt()}"
            val scoreStart = length
            append(scoreText)
            bold?.let {
                setSpan(CustomTypefaceSpan(it), scoreStart, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            setSpan(AbsoluteSizeSpan(36, true), scoreStart, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(Color.parseColor("#73605A")), scoreStart, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


            // 상태 문구
            val statusMessage = when {
                stress == 0f -> "스트레스를 측정하고 있어요.\n" + "조금만 기다려주세요."
                stress < 70f -> "지금 상태는 양호해요. 계속 잘 유지해봐요!"
                else -> "스트레스 지수가 높아요.\n" + "지금은 잠시 쉼표가 필요한 순간이에요."
            }

            append("\n")

            val statusStart = length
            append(statusMessage)
            val regular = ResourcesCompat.getFont(requireContext(), R.font.pretendard_regular)
            regular?.let {
                setSpan(CustomTypefaceSpan(it), statusStart, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            setSpan(AbsoluteSizeSpan(16, true), statusStart, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(Color.parseColor("#73605A")), statusStart, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)}

        binding.stressLabel.text = stressMessage

        return binding.root
    }

    // ✅ SharedPreferences에 저장된 스트레스 데이터 중 가장 최신 것을 반환
    private fun getLatestStoredStress(context: Context): Float? {
        val prefs = context.getSharedPreferences("HeartRateStorage", Context.MODE_PRIVATE)
        val json = prefs.getString("unsentHeartRates", null) ?: return null

        val type = object : TypeToken<List<HeartRateLocal>>() {}.type
        val list: List<HeartRateLocal> = Gson().fromJson(json, type)

        return list.maxByOrNull { it.startTime }?.stress
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
