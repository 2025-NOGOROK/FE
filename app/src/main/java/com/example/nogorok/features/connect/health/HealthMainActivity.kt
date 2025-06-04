package com.example.nogorok.features.connect.health

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.nogorok.R
import com.example.nogorok.features.connect.calendar.CalendarConnectActivity
import com.example.nogorok.features.connect.health.service.HeartRateScheduler
import com.example.nogorok.features.connect.health.utils.AppConstants
import com.example.nogorok.features.connect.health.utils.showToast
import com.example.nogorok.features.connect.health.viewmodel.HealthMainViewModel
import com.example.nogorok.features.connect.health.viewmodel.HealthViewModelFactory
import com.example.nogorok.features.connect.health.viewmodel.HeartRateViewModel
import com.google.android.material.button.MaterialButton
import com.samsung.android.sdk.health.data.permission.AccessType
import com.samsung.android.sdk.health.data.permission.Permission
import com.samsung.android.sdk.health.data.request.DataTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.Lifecycle
import androidx.work.WorkManager

import java.time.LocalDate

class HealthMainActivity : AppCompatActivity() {

    private lateinit var healthMainViewModel: HealthMainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_main)

        healthMainViewModel = ViewModelProvider(
            this, HealthViewModelFactory(this)
        )[HealthMainViewModel::class.java]

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)

        btnBack.setOnClickListener {
            finish()
        }

        btnNext.setOnClickListener {
            requestAllHealthPermissions()
        }

        healthMainViewModel.exceptionResponse.observe(this) { message ->
            showToast(this, message)
        }

        collectResponse()
    }

    private fun requestAllHealthPermissions() {
        val allPermissions = mutableSetOf(
            Permission.of(DataTypes.HEART_RATE, AccessType.READ),
            Permission.of(DataTypes.SLEEP, AccessType.READ),
            Permission.of(DataTypes.BLOOD_OXYGEN, AccessType.READ),
            Permission.of(DataTypes.SKIN_TEMPERATURE, AccessType.READ)
        )
        healthMainViewModel.checkForPermission(
            this,
            allPermissions,
            AppConstants.HEART_RATE_ACTIVITY
        )
    }

    private fun collectResponse() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    healthMainViewModel.permissionResponse.collect { result ->
                        if (result.first == AppConstants.SUCCESS) {
                            val heartRateViewModel = ViewModelProvider(
                                this@HealthMainActivity,
                                HealthViewModelFactory(this@HealthMainActivity)
                            )[HeartRateViewModel::class.java]

                            // ✅ 순차 실행을 보장하며 데이터 업로드
                            withContext(Dispatchers.IO) {
                                heartRateViewModel.readAllHeartRateDataFrom(
                                    startDate = LocalDate.of(2025, 5, 1),
                                    endDate = LocalDate.of(2025, 5, 10)
                                )
                            }

                            // ✅ WorkManager 등록
                            HeartRateScheduler.scheduleHourlyUpload(this@HealthMainActivity)

                            // ✅ 등록 직후 상태 확인 로그
                            WorkManager.getInstance(this@HealthMainActivity)
                                .getWorkInfosForUniqueWorkLiveData("HeartRateUploadWork")
                                .observe(this@HealthMainActivity) { workInfos ->
                                    val state = workInfos?.firstOrNull()?.state
                                    Log.d("WorkManagerStatus", "📦 현재 워커 상태: $state")
                                }

                            // ✅ 다음 화면으로 이동
                            val intent = Intent(this@HealthMainActivity, CalendarConnectActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else if (result.first != AppConstants.WAITING) {
                            showToast(this@HealthMainActivity, result.first)
                        }

                        healthMainViewModel.resetPermissionResponse()
                    }
                }
            }
        }
    }
}
