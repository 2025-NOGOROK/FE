package com.example.nogorok

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class RegisterInputNotificationActivity : AppCompatActivity() {

    private lateinit var btnNext: MaterialButton

    // Android 13 이상에서 권한 요청
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // 팝업에서 허용/거부 누르면 버튼 활성화
        btnNext.isEnabled = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_input_notification)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        btnNext = findViewById(R.id.btnNext)
        btnNext.isEnabled = false

        btnNext.setOnClickListener {
            val intent = Intent(this, RegisterInputCompleteActivity::class.java)
            startActivity(intent)
        }

        // 알림 권한 팝업 띄우기 (최초 진입 시)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // Android 12 이하에서는 바로 버튼 활성화
            btnNext.isEnabled = true
        }
    }
}
