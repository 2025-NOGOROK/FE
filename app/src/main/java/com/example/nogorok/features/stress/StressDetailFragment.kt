// 경로: com.example.nogorok.features.stress.StressDetailFragment.kt
package com.example.nogorok.features.stress

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.nogorok.MainActivity
import com.example.nogorok.R
import com.example.nogorok.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class StressDetailFragment : Fragment(R.layout.fragment_web_detail) {

    private val stressApi: StressApi by lazy { RetrofitClient.stressApi }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val ivThumbnail = view.findViewById<ImageView>(R.id.imgThumbnail)
        val tvTitle = view.findViewById<TextView>(R.id.tvPageTitle)
        val tvMeta = view.findViewById<TextView>(R.id.tvPageMeta)
        val contentContainer = view.findViewById<LinearLayout>(R.id.contentContainer)

        btnBack.setOnClickListener { requireActivity().onBackPressed() }

        val linkKey = arguments?.getString("linkKey") ?: ""
        val thumbnailUrl = arguments?.getString("thumbnailUrl")

        Glide.with(requireContext())
            .load(thumbnailUrl)
            .placeholder(R.drawable.sample)
            .into(ivThumbnail)

        lifecycleScope.launch {
            try {
                when (linkKey) {
                    // 1️⃣ 삼성병원 → API 호출
                    "samsung" -> {
                        val response = stressApi.getStressPage(
                            "http://www.samsunghospital.com/home/healthMedical/private/lifeClinicStress05.do"
                        )
                        val firstItem = response.firstOrNull()
                        tvTitle.text = firstItem?.h3 ?: "제목 없음"
                        tvMeta.text = firstItem?.h5 ?: "출처 없음"

                        contentContainer.removeAllViews()
                        response.forEach { item ->
                            listOfNotNull(item.h3, item.h4, item.h5, item.p).forEach { text ->
                                val tv = TextView(requireContext()).apply {
                                    setTextAppearance(android.R.style.TextAppearance_Medium)
                                    this.text = text
                                    setPadding(0, 16, 0, 16)
                                }
                                contentContainer.addView(tv)
                            }
                            item.image?.split(",")?.map { it.trim() }?.forEach { imgUrl ->
                                val iv = ImageView(requireContext()).apply {
                                    layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                                    adjustViewBounds = true
                                    scaleType = ImageView.ScaleType.CENTER_CROP
                                }
                                Glide.with(requireContext()).load(imgUrl).into(iv)
                                contentContainer.addView(iv)
                            }
                        }
                    }

                    // 2️⃣ 사이언스타임즈 → 하드코딩
                    "science" -> {
                        tvTitle.text = "효과적인 스트레스 해소법: 건강한 일상을 위한 가이드"
                        tvMeta.text = "사이언스타임즈"

                        contentContainer.removeAllViews()
                        val texts = listOf(
                            "스트레스를 줄이고 더 건강한 생활을 시작하기 위한 16가지 방법",
                            "만성적인 스트레스는 심장 질환, 불안 장애, 우울증 등 다양한 건강 문제를 일으킬 수 있다.",
                            "신체 활동과 건강한 식습관으로 스트레스 줄이기",
                            "정신 건강 관리와 명상, 글쓰기, 카페인 조절",
                            "사회적 관계와 경계 설정",
                            "자연과의 교감, 반려동물, 영양제의 도움"
                        )
                        texts.forEach { t ->
                            val tv = TextView(requireContext()).apply {
                                setTextAppearance(android.R.style.TextAppearance_Medium)
                                text = t
                                setPadding(0, 16, 0, 16)
                            }
                            contentContainer.addView(tv)
                        }

                        val images = listOf(
                            "https://www.sciencetimes.co.kr/jnrepo/upload/editor/202503/f8c1e3e053ca4453b59b14d1a2e6dceb_1743368019590.png",
                            "https://www.sciencetimes.co.kr/jnrepo/upload/editor/202503/7357f95f5bc84646a59490ca5bd294c2_1743368033036.png"
                        )
                        images.forEach { url ->
                            val iv = ImageView(requireContext()).apply {
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                adjustViewBounds = true
                                scaleType = ImageView.ScaleType.CENTER_CROP
                            }
                            Glide.with(requireContext()).load(url).into(iv)
                            contentContainer.addView(iv)
                        }
                    }

                    // 3️⃣ 닥터나우 → 하드코딩
                    "doctor" -> {
                        tvTitle.text = "스트레스 해소하는 방법"
                        tvMeta.text = "닥터나우"

                        contentContainer.removeAllViews()
                        val texts = listOf(
                            "꾸준한 운동으로 엔도르핀 분비 → 스트레스 감소",
                            "균형 잡힌 식단과 충분한 수면 → 저항력 강화",
                            "명상과 호흡법 → 심리적 안정",
                            "취미 활동 → 사회적 관계 유지",
                            "전문가 상담 필요 시 의학적 도움"
                        )
                        texts.forEach { t ->
                            val tv = TextView(requireContext()).apply {
                                setTextAppearance(android.R.style.TextAppearance_Medium)
                                text = t
                                setPadding(0, 16, 0, 16)
                            }
                            contentContainer.addView(tv)
                        }

                        val images = listOf(
                            "https://d2m9duoqjhyhsq.cloudfront.net/marketingContents/article/article230-02.jpg",
                            "https://d2m9duoqjhyhsq.cloudfront.net/marketingContents/article/article230-03.jpg"
                        )
                        images.forEach { url ->
                            val iv = ImageView(requireContext()).apply {
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                adjustViewBounds = true
                                scaleType = ImageView.ScaleType.CENTER_CROP
                            }
                            Glide.with(requireContext()).load(url).into(iv)
                            contentContainer.addView(iv)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                tvTitle.text = "데이터 로드 실패"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.apply {
            findViewById<BottomNavigationView>(R.id.navigation_main)?.visibility = View.GONE
            findViewById<View>(R.id.fabMain)?.visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as? MainActivity)?.apply {
            findViewById<BottomNavigationView>(R.id.navigation_main)?.visibility = View.VISIBLE
            findViewById<View>(R.id.fabMain)?.visibility = View.VISIBLE
        }
    }
}
