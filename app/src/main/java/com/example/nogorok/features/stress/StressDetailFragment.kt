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
import com.example.nogorok.R
import com.example.nogorok.network.RetrofitClient
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

        lifecycleScope.launch {
            try {
                val response = stressApi.getStressPage()

                Glide.with(requireContext())
                    .load(response.thumbnailUrl)
                    .placeholder(R.drawable.sample)
                    .into(ivThumbnail)

                tvTitle.text = response.title ?: "제목 없음"
                tvMeta.text = response.meta ?: "출처 정보 없음"
                tvContent.text = response.content ?: "내용 없음"

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
