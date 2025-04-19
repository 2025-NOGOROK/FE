package com.example.nogorok

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HealthTermsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_terms)

        val btnClose = findViewById<ImageButton>(R.id.btnClose)
        val tvContent = findViewById<TextView>(R.id.tvContent)

        btnClose.setOnClickListener { finish() }

        tvContent.text = """
본 동의서는 「개인정보보호법」 제23조(민감정보의 처리 제한)에 따라, '노고록' 앱이 사용자의 생체 정보를 활용하여 맞춤형 일정 추천 서비스를 제공하기 위해 필요한 사항을 안내하고 동의를 받기 위한 것입니다.
이용자는 민감정보 수집 및 이용에 동의하지 않을 권리가 있으며, 다만 해당 동의가 없을 경우 본 서비스의 핵심 기능을 이용할 수 없습니다.

1. 수집 항목
- 스트레스 지수 (심박변이도 기반)
- 심박수 및 생체신호 등 삼성 웨어러블 기기를 통해 연동된 건강정보

2. 수집 목적
- 스트레스 상태에 기반한 일정 추천 및 루틴 제안
- 맞춤형 리포트 제공 및 분석 기능
- 사용자 상태 변화에 따른 일정 조정 기능 제공

3. 보유 및 이용 기간
- 회원 탈퇴 또는 서비스 이용 종료 시까지 보관하며, 이후에는 지체 없이 파기합니다.
- 법령에 따라 보존이 필요한 경우 해당 법령이 정한 기간 동안 보관할 수 있습니다.

4. 동의 거부 권리 및 불이익
- 이용자는 동의를 거부할 수 있으며, 다만 해당 정보는 서비스의 본질적 기능 제공을 위한 필수 항목으로, 동의하지 않을 경우 서비스 이용이 제한됩니다.
        """.trimIndent()
    }
}
