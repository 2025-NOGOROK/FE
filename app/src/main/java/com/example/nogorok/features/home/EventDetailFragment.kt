package com.example.nogorok.features.event

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
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import kotlin.math.*

class EventDetailFragment : Fragment(R.layout.fragment_event_detail) {

    private val eventApi: EventApi by lazy { RetrofitClient.eventApi }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뒤로가기 버튼
        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            requireActivity().onBackPressed()
        }

        val eventSeq = arguments?.getInt("eventSeq") ?: return

        val ivThumbnail = view.findViewById<ImageView>(R.id.ivEventThumbnail)
        val chipGenre   = view.findViewById<Chip>(R.id.chipEventGenre)
        val tvTitle     = view.findViewById<TextView>(R.id.tvEventTitle)
        val tvPeriod    = view.findViewById<TextView>(R.id.tvEventPeriod)
        val tvDistance  = view.findViewById<TextView>(R.id.tvDistance)
        val tvAddress   = view.findViewById<TextView>(R.id.tvAddress)
        val tvDesc      = view.findViewById<TextView>(R.id.tvDescription)

        lifecycleScope.launch {
            try {
                val response = eventApi.getEventDetail(eventSeq)
                val item = response.response.body.items.item

                Glide.with(requireContext())
                    .load(item.imgUrl)
                    .placeholder(R.drawable.sample)
                    .error(R.drawable.sample)
                    .into(ivThumbnail)

                chipGenre.text = item.realmName ?: "공연/전시"
                tvTitle.text = item.title ?: "제목 없음"
                tvPeriod.text = "${item.startDate ?: ""} ~ ${item.endDate ?: ""}"
                tvAddress.text = item.placeAddr ?: "주소 정보 없음"
                tvDesc.text = item.contents1 ?: "설명 없음"

                // 거리 계산
                val userLat = 37.5665
                val userLng = 126.9780
                val dist = calculateDistance(
                    userLat, userLng,
                    item.gpsY ?: 0.0,
                    item.gpsX ?: 0.0
                )
                tvDistance.text = "현 위치로부터 ${"%.1f".format(dist)} km"

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }
}
