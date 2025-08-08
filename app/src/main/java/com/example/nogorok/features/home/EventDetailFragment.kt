package com.example.nogorok.features.event

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.nogorok.R

class EventDetailFragment : Fragment(R.layout.fragment_event_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뒤로가기 버튼
        view.findViewById<ImageButton>(R.id.btnBack)
            .setOnClickListener { requireActivity().onBackPressed() }

        // TODO: 아래에 실제 데이터(썸네일, 제목, 거리, 주소, 설명 등) 바인딩 로직을 추가하세요.
        // 예시:
        // val ivThumbnail = view.findViewById<ImageView>(R.id.ivEventThumbnail)
        // val tvTitle     = view.findViewById<TextView>(R.id.tvEventTitle)
        // ...
        // ivThumbnail.load(event.thumbnailUrl)
        // tvTitle.text = event.title
    }
}
