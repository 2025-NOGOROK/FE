// 경로: com.example.nogorok.features.stress.StressDetailFragment.kt
package com.example.nogorok.features.stress

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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
        val tvContent = view.findViewById<TextView>(R.id.tvContent)

        btnBack.setOnClickListener { requireActivity().onBackPressed() }

        val linkUrl = arguments?.getString("linkUrl")
        val thumbnailUrl = arguments?.getString("thumbnailUrl")

        // ✅ 상단 썸네일 고정
        Glide.with(requireContext())
            .load(thumbnailUrl)
            .placeholder(R.drawable.sample)
            .error(R.drawable.sample)
            .into(ivThumbnail)

        lifecycleScope.launch {
            try {
                if (linkUrl != null) {
                    val response = stressApi.getStressPage(linkUrl)

                    // ✅ 본문이 배열일 경우 전부 합치기
                    val contentBuilder = StringBuilder()
                    response.forEach { item ->
                        item.h3?.let { contentBuilder.append("\n# $it\n") }
                        item.h4?.let { contentBuilder.append("## $it\n") }
                        item.h5?.let { contentBuilder.append("### $it\n") }
                        item.p?.let { contentBuilder.append("${it}\n\n") }
                    }

                    tvTitle.text = response.firstOrNull()?.h3 ?: "제목 없음"
                    tvMeta.text = response.firstOrNull()?.h5 ?: "출처 없음"
                    tvContent.text = contentBuilder.toString()
                } else {
                    tvTitle.text = "잘못된 요청"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                tvTitle.text = "데이터 로드 실패"
                tvContent.text = e.localizedMessage
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
