package com.example.nogorok.features.home

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.nogorok.R
import com.example.nogorok.databinding.FragmentHomeBinding
import com.example.nogorok.utils.CustomTypefaceSpan
import com.example.nogorok.network.dto.TourItem

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        observeViewModel()
        viewModel.fetchLatestStress()
        viewModel.fetchTourByLocation(x = 126.9723, y = 37.5547)
        viewModel.fetchTraumaArticle()
        viewModel.fetchSamsungStress()


        return binding.root
    }

    private fun observeViewModel() {
        viewModel.stress.observe(viewLifecycleOwner) { stress ->
            binding.stressLabel.text = createStressMessage(stress)
        }

        viewModel.tourList.observe(viewLifecycleOwner) { tourList ->
            Log.d("HomeFragment", "받아온 tourList size: ${tourList.size}")
            updateTourListUI(tourList)
        }

    }

    private fun updateTourListUI(tourList: List<TourItem>) {
        val container = binding.tourListContainer
        container.removeAllViews()

        tourList.forEach { item ->
            val context = requireContext()

            val frameLayout = FrameLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(168.dp, 200.dp).apply {
                    marginEnd = 12.dp
                }
                clipToOutline = true
                background = ContextCompat.getDrawable(context, R.drawable.rounded_item_background)
                outlineProvider = ViewOutlineProvider.BACKGROUND
            }

            val imageView = ImageView(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            val titleOverlay = TextView(context).apply {
                text = item.title
                setTextColor(Color.WHITE)
                textSize = 14f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(10.dp, 10.dp, 10.dp, 10.dp)
                background = ContextCompat.getDrawable(context, R.drawable.overlay_background)
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = android.view.Gravity.BOTTOM
                }
            }

            val imageUrl = when {
                !item.firstImage.isNullOrBlank() -> item.firstImage
                !item.firstImage2.isNullOrBlank() -> item.firstImage2
                else -> null
            }

            Glide.with(this)
                .load(imageUrl ?: R.drawable.sample)
                .into(imageView)

            frameLayout.addView(imageView)
            frameLayout.addView(titleOverlay)

            container.addView(frameLayout)
        }
    }

    private fun createStressMessage(stress: Float): SpannableStringBuilder {
        val label = "당신의 최근 스트레스 지수는\n"
        val stressMessage = SpannableStringBuilder().apply {
            append(label)

            val semiBoldTypeface = ResourcesCompat.getFont(requireContext(), R.font.pretendard_semibold)
            semiBoldTypeface?.let {
                setSpan(CustomTypefaceSpan(it), 0, label.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            setSpan(
                ForegroundColorSpan(Color.parseColor("#73605A")),
                0,
                label.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.love)
            drawable?.setBounds(0, 0, 100, 86)
            val imageSpan = drawable?.let { ImageSpan(it, ImageSpan.ALIGN_BASELINE) }

            val imageStart = length
            append("❤️")
            if (imageSpan != null) {
                setSpan(imageSpan, imageStart, imageStart + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            val boldTypeface = ResourcesCompat.getFont(requireContext(), R.font.pretendard_bold)
            val scoreText = " ${stress.toInt()}"
            val scoreStart = length
            append(scoreText)
            boldTypeface?.let {
                setSpan(CustomTypefaceSpan(it), scoreStart, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            setSpan(AbsoluteSizeSpan(36, true), scoreStart, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(Color.parseColor("#73605A")), scoreStart, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            val statusMessage = when {
                stress == 0f -> "스트레스를 측정하고 있어요.\n조금만 기다려주세요."
                stress < 70f -> "지금 상태는 양호해요. 계속 잘 유지해봐요!"
                else -> "스트레스 지수가 높아요.\n지금은 잠시 쉼표가 필요한 순간이에요."
            }

            append("\n")

            val statusStart = length
            append(statusMessage)

            val regularTypeface = ResourcesCompat.getFont(requireContext(), R.font.pretendard_regular)
            regularTypeface?.let {
                setSpan(CustomTypefaceSpan(it), statusStart, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            setSpan(AbsoluteSizeSpan(16, true), statusStart, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(Color.parseColor("#73605A")), statusStart, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return stressMessage
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 확장 함수 (dp to px)
    val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()
}
