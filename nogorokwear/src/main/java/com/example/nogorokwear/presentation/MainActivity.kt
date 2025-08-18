package com.example.nogorokwear.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

private const val TAG = "HR-UI"

/**
 * 메인 액티비티는 역할을 최소화하고:
 * 1) 권한(BODY_SENSORS, POST_NOTIFICATIONS) 확인/요청
 * 2) 모두 허용되면 HrForegroundService 를 시작
 * 3) UI가 필요 없다면 finish() 로 종료
 *
 * ※ 실제 센서 수집/계산은 HrForegroundService 에서 수행됩니다.
 */
class MainActivity : ComponentActivity() {

    private var sensorsGranted = false
    private var notificationsGranted = false

    // 센서 권한 런처 (Android 13+ 에서 런타임 권한)
    private val requestSensorsPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            sensorsGranted = granted || Build.VERSION.SDK_INT < 33
            if (!granted && Build.VERSION.SDK_INT >= 33) {
                Toast.makeText(this, "센서 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
            maybeStartService()
        }

    // 알림 권한 런처 (Android 13+ 에서 런타임 권한)
    private val requestNotificationsPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            notificationsGranted = granted || Build.VERSION.SDK_INT < 33
            if (!granted && Build.VERSION.SDK_INT >= 33) {
                Toast.makeText(this, "알림 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
            maybeStartService()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 현재 권한 상태 미리 반영
        sensorsGranted = hasPermission(Manifest.permission.BODY_SENSORS) || Build.VERSION.SDK_INT < 33
        notificationsGranted = hasPermission(Manifest.permission.POST_NOTIFICATIONS) || Build.VERSION.SDK_INT < 33

        // 필요한 권한 요청
        if (!sensorsGranted && Build.VERSION.SDK_INT >= 33) {
            requestSensorsPermission.launch(Manifest.permission.BODY_SENSORS)
        }
        if (!notificationsGranted && Build.VERSION.SDK_INT >= 33) {
            requestNotificationsPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // 이미 모두 허용돼 있으면 바로 시작
        maybeStartService()

        // UI 없이 자동 시작만 하면 액티비티 종료
        finish()
    }

    /** 모든 전제 조건이 갖춰지면 포그라운드 서비스 시작 */
    private fun maybeStartService() {
        val ready =
            if (Build.VERSION.SDK_INT >= 33) sensorsGranted && notificationsGranted
            else true

        if (ready) {
            Log.i(TAG, "Starting HrForegroundService")
            val intent = Intent(this, HrForegroundService::class.java)
            // Android 8.0+ 는 startForegroundService 권장
            ContextCompat.startForegroundService(this, intent)
        } else {
            Log.i(TAG, "Permissions not ready. sensors=$sensorsGranted, notifications=$notificationsGranted")
        }
    }

    /** (옵션) 설정/디버그용으로 서비스 중지하고 싶을 때 사용 */
    private fun stopHrService() {
        val intent = Intent(this, HrForegroundService::class.java)
        stopService(intent)
    }

    private fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}
