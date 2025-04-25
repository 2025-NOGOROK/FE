/*
 * Copyright (C) 2024 Samsung Electronics Co., Ltd. All rights reserved
 */
package com.example.nogorok.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nogorok.utils.AppConstants
import com.example.nogorok.utils.getExceptionHandler
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.permission.AccessType
import com.samsung.android.sdk.health.data.permission.Permission
import com.samsung.android.sdk.health.data.request.DataTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HealthMainViewModel(private val healthDataStore: HealthDataStore, activity: Activity) :
    ViewModel() {

    private val _permissionResponse = MutableStateFlow(Pair(AppConstants.WAITING, -1))
    private val _exceptionResponse: MutableLiveData<String> = MutableLiveData<String>()
    private val exceptionHandler = getExceptionHandler(activity, _exceptionResponse)
    val permissionResponse: StateFlow<Pair<String, Int>> = _permissionResponse
    val exceptionResponse: LiveData<String> = _exceptionResponse

    fun checkForPermission(
        context: Context,
        permSet: MutableSet<Permission>,
        activityId: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val grantedPermissions = healthDataStore.getGrantedPermissions(permSet)

            if (grantedPermissions.containsAll(permSet)) {
                _permissionResponse.emit(Pair(AppConstants.SUCCESS, activityId))
            } else {
                requestForPermission(context, permSet, activityId)
            }
        }
    }

    private fun requestForPermission(
        context: Context,
        permSet: MutableSet<Permission>,
        activityId: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val activity = context as Activity
            val result = healthDataStore.requestPermissions(permSet, activity)
            Log.i(TAG, "requestPermissions: Success ${result.size}")

            if (result.containsAll(permSet)) {
                _permissionResponse.emit(Pair(AppConstants.SUCCESS, activityId))
            } else {
                withContext(Dispatchers.Main) {
                    _permissionResponse.emit(Pair(AppConstants.NO_PERMISSION, -1))
                    Log.i(TAG, "requestPermissions: NO_PERMISSION")
                }
            }
        }
    }

    // Permissions for all data types accessed in this application
    fun connectToSamsungHealth(context: Context) {
        val permSet = setOf(
            Permission.of(DataTypes.STEPS, AccessType.READ),
            Permission.of(DataTypes.SLEEP, AccessType.READ),
            Permission.of(DataTypes.BLOOD_OXYGEN, AccessType.READ),
            Permission.of(DataTypes.SKIN_TEMPERATURE, AccessType.READ),
            Permission.of(DataTypes.NUTRITION, AccessType.READ),
            Permission.of(DataTypes.HEART_RATE, AccessType.READ),
            Permission.of(DataTypes.NUTRITION, AccessType.WRITE)
        )
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            healthDataStore.requestPermissions(permSet, context as Activity)
        }
    }

    fun resetPermissionResponse() {
        viewModelScope.launch {
            _permissionResponse.emit(Pair(AppConstants.WAITING, -1))
        }
    }

    companion object {
        private const val TAG = "[HTK]HealthDiaryViewModel"
    }
}
