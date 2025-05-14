package com.example.nogorok

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ServiceTermsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_terms)

        val btnClose = findViewById<ImageButton>(R.id.btnClose)
        val tvContent = findViewById<TextView>(R.id.tvContent)

        btnClose.setOnClickListener { finish() }

        // 약관 내용 세팅 (여기서 네가 준 텍스트 그대로)
        tvContent.text = """
제1조 (목적)
이 약관은 “노고록” 앱(이하 "회사")이 제공하는 스트레스 기반 일정 추천 서비스의 이용 조건 및 절차, 이용자와 회사 간의 권리·의무 등을 규정함을 목적으로 합니다.

제2조 (정의)
“서비스”란 사용자의 생체정보 및 입력정보를 바탕으로 일정 추천, 리포트 제공, 루틴 설계를 제공하는 기능을 말합니다.
“이용자”란 본 약관에 동의하고 서비스를 사용하는 회원을 말합니다.

제3조 (약관의 효력 및 변경)
본 약관은 앱 내 고지 후 효력을 발생하며, 회사는 관련 법령에 따라 약관을 변경할 수 있습니다.
변경 시 이용자에게 사전 고지하며, 이용자가 변경 약관에 동의하지 않을 경우 서비스 이용을 중단할 수 있습니다.

제4조 (서비스의 제공 및 변경)
회사는 사용자의 스트레스 지수, 위치 정보, 설문 결과 등을 바탕으로 맞춤 일정을 추천하는 기능을 제공합니다.
서비스는 기술적 필요에 따라 사전 고지 후 변경될 수 있습니다.

제5조 (서비스 이용의 제한 및 중단)
회사는 천재지변, 시스템 점검 등 불가피한 사유가 있을 경우 서비스 제공을 일시 중단할 수 있으며, 사전 또는 사후에 공지합니다.

제6조 (회원 탈퇴 및 이용 계약의 해지)
이용자는 언제든지 앱 내 설정을 통해 탈퇴할 수 있으며, 회사는 관련 법령에 따라 개인정보를 처리합니다.

제7조 (면책 조항)
회사는 사용자의 생체정보에 기반한 일정 추천이 의학적 진단이나 치료를 대체하지 않음을 고지하며, 서비스 이용으로 발생하는 결과에 대해 법적 책임을 지지 않습니다.
        """.trimIndent()
    }
}