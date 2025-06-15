package com.example.nogorok.features.home

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.util.Log
import android.view.*
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.nogorok.R
import com.example.nogorok.databinding.FragmentHomeBinding
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
        viewModel.fetchTraumaArticle()
        viewModel.fetchSamsungStress()
        viewModel.fetchLawTimes()

        getCurrentLocation()

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
                            Log.e("HomeFragment", "\uC704\uCE58\uB97C \uAC00\uC9C0\uC624\uC9C0 \uBABB\uD588\uC2B5\uB2C8\uB2E4.")
                        }
                    }
                    .addOnFailureListener {
                        Log.e("HomeFragment", "\uC704\uCE58 \uC694\uCCAD \uC2E4\uD328: ${it.message}")
                    }
            } catch (e: SecurityException) {
                Log.e("HomeFragment", "\uC704\uCE58 \uAD8C\uD55C \uC608\uC678 \uBC1C\uC0DD: ${e.message}")
            }
        } else {
            Log.e("HomeFragment", "\uC704\uCE58 \uAD8C\uD55C \uC5C6\uC74C. \uD638\uCD9C\uD558\uC9C0 \uC54A\uC74C.")
        }
    }

    private fun loadImageOrSample(imageView: ImageView, imageUrl: String?) {
        Glide.with(this)
            .load(imageUrl ?: R.drawable.sample)
            .placeholder(R.drawable.sample)
            .error(R.drawable.sample)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }

    private fun observeViewModel() {
        viewModel.stress.observe(viewLifecycleOwner) {
            binding.stressLabel.text = createStressMessage(it)
        }

        viewModel.tourList.observe(viewLifecycleOwner) {
            updateTourListUI(it)
        }

        viewModel.samsungStress.observe(viewLifecycleOwner) {
            loadImageOrSample(binding.ivSamsungStress, it)
        }

        viewModel.trauma.observe(viewLifecycleOwner) {
            loadImageOrSample(binding.ivTrauma, it)
        }

        viewModel.lawtimes.observe(viewLifecycleOwner) {
            loadImageOrSample(binding.ivLawtimes, it)
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
