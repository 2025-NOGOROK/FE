package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity

class StressLoadingActivity : AppCompatActivity() {

    private lateinit var btnNext: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stress_loading)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        btnNext = findViewById(R.id.btnNext)
        btnNext.isEnabled = false  // 처음에는 비활성화

        // "다음" 버튼 클릭 시 캘린더 연결 액티비티로 이동
        btnNext.setOnClickListener {
            val intent = Intent(this, CalendarConnectActivity::class.java)
            startActivity(intent)
        }

        // 액티비티 시작 시 모달 자동으로 띄우기
        showSamsungHealthPermissionDialog()
    }

    private fun showSamsungHealthPermissionDialog() {
        val dialog = SamsungHealthPermissionBottomSheet(
            onComplete = {
                btnNext.isEnabled = true  // 완료 시 다음 버튼 활성화
            },
            onCancel = {
                // 취소 시 아무것도 하지 않음 또는 다른 처리 가능
            }
        )
        dialog.show(supportFragmentManager, "SamsungHealthPermission")
    }
}