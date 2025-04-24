package com.example.nogorok

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class RegisterInputNotificationActivity : AppCompatActivity() {

    // Android 13 이상에서 권한 요청
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // 권한 허용/거부 결과 처리 (여기선 그냥 넘어감)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_input_notification)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val btnNext = findViewById<MaterialButton>(R.id.btnNext)

        // 버튼 스타일(항상 활성화, 피그마 색상)
        btnNext.setBackgroundColor(Color.parseColor("#73605A"))
        btnNext.setTextColor(Color.parseColor("#F4EED4"))
        btnNext.strokeColor = ColorStateList.valueOf(Color.parseColor("#FF4D403C"))

        btnNext.setOnClickListener {
            // Android 13 이상에서 알림 권한 요청
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
            // 다음 단계(가입 완료 화면)로 이동
            val intent = Intent(this, RegisterInputCompleteActivity::class.java)
            startActivity(intent)
        }
    }
}
