/*
 * Copyright (C) 2024 Samsung Electronics Co., Ltd. All rights reserved
 */
package com.example.nogorok.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.nogorok.R
import com.example.nogorok.databinding.HealthMainBinding
import com.example.nogorok.utils.showToast
import com.example.nogorok.utils.AppConstants
import com.example.nogorok.viewmodel.HealthMainViewModel
import com.example.nogorok.viewmodel.HealthViewModelFactory
import com.samsung.android.sdk.health.data.helper.SdkVersion
import com.samsung.android.sdk.health.data.permission.AccessType
import com.samsung.android.sdk.health.data.permission.Permission
import com.samsung.android.sdk.health.data.request.DataTypes
import kotlinx.coroutines.launch

class HealthMainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var healthMainViewModel: HealthMainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        healthMainViewModel = ViewModelProvider(
            this, HealthViewModelFactory(this)
        )[HealthMainViewModel::class.java]

        /**  Initialize OnClickListener on Heart Rate, Sleep, Nutrition and Step buttons and set sdk version */
        DataBindingUtil
            .setContentView<HealthMainBinding>(this, R.layout.health_main)
            .run {
                cvNutrition.setOnClickListener(this@HealthMainActivity)
                cvStep.setOnClickListener(this@HealthMainActivity)
                cvHeartRate.setOnClickListener(this@HealthMainActivity)
                cvSleep.setOnClickListener(this@HealthMainActivity)
                versionValue.text = SdkVersion.getVersionName()
            }

        /** Show toast on exception occurrence **/
        healthMainViewModel.exceptionResponse.observe(this) { message ->
            showToast(this, message)
        }
        collectResponse()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.connect_samsung_health, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.permission -> {
                healthMainViewModel.connectToSamsungHealth(this)
                true
            }

            else -> false
        }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.cv_nutrition -> {
                val permSet = mutableSetOf(
                    Permission.of(DataTypes.NUTRITION, AccessType.READ)
                )
                healthMainViewModel.checkForPermission(
                    this,
                    permSet,
                    AppConstants.NUTRITION_ACTIVITY
                )
            }

            R.id.cv_step -> {
                val permSet = mutableSetOf(
                    Permission.of(DataTypes.STEPS, AccessType.READ)
                )
                healthMainViewModel.checkForPermission(this, permSet, AppConstants.STEP_ACTIVITY)
            }

            R.id.cv_heart_rate -> {
                val permSet = mutableSetOf(
                    Permission.of(DataTypes.HEART_RATE, AccessType.READ)
                )
                healthMainViewModel.checkForPermission(
                    this,
                    permSet,
                    AppConstants.HEART_RATE_ACTIVITY
                )
            }

            R.id.cv_sleep -> {
                val permSet = mutableSetOf(
                    Permission.of(DataTypes.SLEEP, AccessType.READ),
                    Permission.of(DataTypes.BLOOD_OXYGEN, AccessType.READ),
                    Permission.of(DataTypes.SKIN_TEMPERATURE, AccessType.READ)
                )
                healthMainViewModel.checkForPermission(this, permSet, AppConstants.SLEEP_ACTIVITY)
            }
        }
    }

    private fun collectResponse() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                /**  Handle response of permission request */
                launch {
                    healthMainViewModel.permissionResponse.collect { result ->
                        if (result.first == AppConstants.SUCCESS) {
                            launchRespectiveActivity(result.second)
                        } else if (result.first != AppConstants.WAITING) {
                            showToast(this@HealthMainActivity, result.first)
                        }
                        healthMainViewModel.resetPermissionResponse()
                    }
                }
            }
        }
    }

    private fun launchRespectiveActivity(activityId: Int) {
        val intent = when (activityId) {
            AppConstants.NUTRITION_ACTIVITY -> Intent(this, NutritionActivity::class.java)
            AppConstants.STEP_ACTIVITY -> Intent(this, StepActivity::class.java)
            AppConstants.HEART_RATE_ACTIVITY -> Intent(this, HeartRateActivity::class.java)
            AppConstants.SLEEP_ACTIVITY -> Intent(this, SleepActivity::class.java)
            else -> null
        }
        if (intent != null) {
            startActivity(intent)
        }
    }
}
