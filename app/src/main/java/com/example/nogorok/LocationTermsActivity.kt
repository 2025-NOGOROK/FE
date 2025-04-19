package com.example.nogorok

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LocationTermsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_terms)

        val btnClose = findViewById<ImageButton>(R.id.btnClose)
        val tvContent = findViewById<TextView>(R.id.tvContent)

        btnClose.setOnClickListener { finish() }

        tvContent.text = """
본 동의서는 「위치정보의 보호 및 이용 등에 관한 법률」 제18조에 따라 '노고록' 앱이 사용자에게 위치기반 서비스를 제공하기 위해 필요한 사항을 안내하고 동의를 받기 위한 것입니다.

1. 수집 항목
- 실시간 위치 정보(GPS 기반)
- 지역 기반 API 호출을 위한 지역 코드 및 위치 좌표

2. 수집 목적
- 사용자 위치에 따른 맞춤 일정 추천
- 날씨·문화행사 등 위치 기반 외부 정보 제공
- 사용자 주변 환경에 맞는 스트레스 해소 활동 제안

3. 보유 및 이용 기간
- 실시간 위치 정보는 수집 즉시 사용되며, 저장하지 않습니다.
- 단, 일정 추천 이력 등 분석을 위한 최소한의 위치 범주 정보는 통계 처리 후 보관될 수 있습니다.

4. 동의 거부 권리 및 불이익
- 이용자는 동의를 거부할 수 있으며, 다만 위치 기반 서비스를 제공받지 못할 경우 일부 기능(예: 야외 일정 추천, 날씨 연동 등)의 이용이 제한될 수 있습니다.
        """.trimIndent()
    }
}
