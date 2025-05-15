package com.example.nogorok

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PrivacyTermsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_terms)

        val btnClose = findViewById<ImageButton>(R.id.btnClose)
        val tvContent = findViewById<TextView>(R.id.tvContent)

        btnClose.setOnClickListener { finish() }

        tvContent.text = """
본 동의서는 「개인정보보호법」 제15조, 제22조에 따라 '노고록' 앱이 제공하는 스트레스 기반 일정 추천 서비스와 관련하여 이용자의 개인정보를 수집·이용하기 위해 필요한 사항을 안내하고 동의를 받기 위한 것입니다.
이용자는 개인정보 수집 및 이용에 동의하지 않을 권리가 있으며, 다만 필수 항목에 대한 동의를 거부할 경우 서비스 이용이 제한될 수 있습니다.

1. 수집 항목
- 일반정보: 이름, 생년, 성별, 이메일 주소
- 서비스 이용정보: 일정 등록 및 완료 내역, 루틴 설정 정보 등
- 기기 정보: 단말기 운영체제, 기기 모델, OS 버전 등

2. 수집 목적
- 맞춤형 일정 추천 및 루틴 분석 서비스 제공
- 사용자 식별 및 서비스 내 기능 이용
- 서비스 이용 통계 분석 및 품질 개선
- 고객 문의 및 민원 처리

3. 보유 및 이용 기간
- 회원 탈퇴 시까지 보관하며, 이후에는 지체 없이 파기합니다.
- 단, 관계 법령에 따라 보존이 필요한 경우 해당 법령이 정한 기간 동안 보관합니다.

4. 동의 거부 권리 및 불이익
- 이용자는 개인정보 수집 및 이용에 동의하지 않을 수 있습니다.
- 다만, 필수 항목에 대한 동의가 없을 경우 서비스 가입 및 이용이 제한될 수 있습니다.
        """.trimIndent()
    }
}