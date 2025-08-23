package com.example.nogorokwear.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val pad = (16 * resources.displayMetrics.density).toInt()
            setPadding(pad, pad, pad, pad)
        }
        val status = TextView(this).apply { text = "노고록 워치 · 준비됨" }
        val btnStart = Button(this).apply { text = "측정 시작" }
        val btnStop  = Button(this).apply { text = "측정 중지" }
        root.addView(status); root.addView(btnStart); root.addView(btnStop)
        setContentView(root)

        btnStart.setOnClickListener {
            if (ensurePermissions()) {
                HrForegroundService.start(this)
                status.text = "측정 중…"
            }
        }
        btnStop.setOnClickListener {
            HrForegroundService.stop(this)
            status.text = "중지됨"
        }

        // 자동 시작을 원하면:
        // if (ensurePermissions()) HrForegroundService.start(this)
    }

    private fun ensurePermissions(): Boolean {
        // 일부 기기/OS에서는 BODY_SENSORS가 필요합니다.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(arrayOf(Manifest.permission.BODY_SENSORS), 100)
                return false
            }
        }
        // (선택) 알림 권한 — Wear OS 4+ 에서 필요할 수 있음
        if (Build.VERSION.SDK_INT >= 33) {
            val p = "android.permission.POST_NOTIFICATIONS"
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(p), 101)
                return false
            }
        }
        return true
    }
}
