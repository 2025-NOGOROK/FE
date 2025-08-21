package com.example.nogorok.features.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.util.Log
import android.view.*
import android.view.ViewOutlineProvider
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.nogorok.R
import com.example.nogorok.databinding.FragmentHomeBinding
import com.example.nogorok.features.survey.BannerSurveyActivity
import com.example.nogorok.network.dto.TourItem
import com.example.nogorok.utils.CustomTypefaceSpan
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        observeViewModel()
        viewModel.fetchLatestStress()
        getCurrentLocation()
        showTourPlaceholders()
        setupStressCardClickListeners()

        binding.bannerSurvey.setOnClickListener {
            startActivity(Intent(requireContext(), BannerSurveyActivity::class.java))
        }

        return binding.root
    }

    private fun getCurrentLocation() {
        val fine = Manifest.permission.ACCESS_FINE_LOCATION
        val coarse = Manifest.permission.ACCESS_COARSE_LOCATION

        if (ContextCompat.checkSelfPermission(requireContext(), fine) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), coarse) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            viewModel.fetchTourByLocation(location.longitude, location.latitude)
                        } else {
                            Log.e("HomeFragment", "위치를 가져오지 못했습니다.")
                        }
                    }
                    .addOnFailureListener {
                        Log.e("HomeFragment", "위치 요청 실패: ${it.message}")
                    }
            } catch (e: SecurityException) {
                Log.e("HomeFragment", "위치 권한 예외 발생: ${e.message}")
            }
        } else {
            Log.e("HomeFragment", "위치 권한 없음. 호출하지 않음.")
        }
    }

    private fun observeViewModel() {
        viewModel.stress.observe(viewLifecycleOwner) {
            binding.stressLabel.text = createStressMessage(it)
        }

        viewModel.tourList.observe(viewLifecycleOwner) {
            updateTourListUI(it)
        }

        // ✅ 삼성병원 카드
        Glide.with(this)
            .load("http://www.samsunghospital.com/_newhome/ui/health_center/static/img/checkup-after/after-clinic-stress05-img01.png")
            .placeholder(R.drawable.sample) // 노고록 기본 이미지
            .error(R.drawable.sample)
            .into(binding.ivSamsungStress)

        // ✅ 사이언스타임즈 카드
        Glide.with(this)
            .load("https://www.sciencetimes.co.kr/jnrepo/upload/editor/202503/f8c1e3e053ca4453b59b14d1a2e6dceb_1743368019590.png")
            .placeholder(R.drawable.sample)
            .error(R.drawable.sample)
            .into(binding.ivLawtimes)

        // ✅ 코리아.kr 카드 (이미지 없음 → 노고록 기본 이미지로 대체)
        Glide.with(this)
            .load(R.drawable.sample)
            .into(binding.ivTrauma)
    }

    private fun setupStressCardClickListeners() {
        binding.containerSamsung.setOnClickListener {
            openWebView("https://www.samsunghospital.com/home/healthMedical/private/lifeClinicStress05.do")
        }

        binding.containerLawtimes.setOnClickListener {
            openWebView("https://www.sciencetimes.co.kr/nscvrg/view/menu/251?searchCategory=223&nscvrgSn=260043")
        }

        binding.containerTrauma.setOnClickListener {
            openWebView("https://www.korea.kr/news/healthView.do?newsId=148855677")
        }
    }

    private fun openWebView(url: String) {
        val intent = Intent(requireContext(), StressWebActivity::class.java)
        intent.putExtra("linkUrl", url)
        startActivity(intent)
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
                    gravity = Gravity.BOTTOM
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

    private fun addBlankEventSlot(parent: LinearLayout) {
        val context = requireContext()
        val frame = FrameLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(168.dp, 200.dp).apply { marginEnd = 12.dp }
            clipToOutline = true
            background = ContextCompat.getDrawable(context, R.drawable.rounded_item_background)
            outlineProvider = ViewOutlineProvider.BACKGROUND
        }
        parent.addView(frame)
    }

    private fun showTourPlaceholders() {
        val parent = binding.tourListContainer
        parent.removeAllViews()
        addBlankEventSlot(parent)
        addBlankEventSlot(parent)
        addBlankEventSlot(parent)
    }

    private fun createStressMessage(stress: Float): SpannableStringBuilder {
        val label = "당신의 최근 스트레스 지수는\n"
        val stressMessage = SpannableStringBuilder().apply {
            append(label)
            val semiBold = ResourcesCompat.getFont(requireContext(), R.font.pretendard_semibold)
            semiBold?.let { setSpan(CustomTypefaceSpan(it), 0, label.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) }
            setSpan(ForegroundColorSpan(Color.parseColor("#73605A")), 0, label.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.love)
            drawable?.setBounds(0, 0, 100, 86)
            val imageSpan = drawable?.let { ImageSpan(it, ImageSpan.ALIGN_BASELINE) }
            val imageStart = length
            append("❤️")
            imageSpan?.let { setSpan(it, imageStart, imageStart + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) }

            val bold = ResourcesCompat.getFont(requireContext(), R.font.pretendard_bold)
            val scoreText = " ${stress.toInt()}"
            val scoreStart = length
            append(scoreText)
            bold?.let { setSpan(CustomTypefaceSpan(it), scoreStart, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) }
            setSpan(AbsoluteSizeSpan(36, true), scoreStart, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(Color.parseColor("#73605A")), scoreStart, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            append("\n")
            val statusMessage = when {
                stress == 0f -> "스트레스를 측정하고 있어요.\n조금만 기다려주세요."
                stress < 70f -> "지금 상태는 양호해요. 계속 잘 유지해봐요!"
                else -> "스트레스 지수가 높아요.\n지금은 잠시 쉼표가 필요한 순간이에요."
            }
            val statusStart = length
            append(statusMessage)
            val regular = ResourcesCompat.getFont(requireContext(), R.font.pretendard_regular)
            regular?.let { setSpan(CustomTypefaceSpan(it), statusStart, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) }
            setSpan(AbsoluteSizeSpan(16, true), statusStart, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(Color.parseColor("#73605A")), statusStart, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return stressMessage
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()
}
