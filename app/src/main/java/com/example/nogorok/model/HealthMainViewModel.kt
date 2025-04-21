package com.example.nogorok.model

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.permission.AccessType
import com.samsung.android.sdk.health.data.permission.Permission
import com.samsung.android.sdk.health.data.request.DataTypes
import com.example.nogorok.utils.AppConstants
import com.example.nogorok.utils.getExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HealthMainViewModel(
    private val store: HealthDataStore,
    private val activity: Activity
) : ViewModel() {

    private val _permissionResponse = MutableStateFlow(Pair(AppConstants.WAITING, -1))
    val permissionResponse: StateFlow<Pair<String, Int>> = _permissionResponse

    private val _exceptionResponse = MutableLiveData<String>()
    val exceptionResponse: LiveData<String> = _exceptionResponse

    private val exceptionHandler = getExceptionHandler(activity, _exceptionResponse)

    /**
     * 심박수 권한 확인 및 요청
     */
    fun checkForPermission(context: Context, activityId: Int) {
        val permSet = mutableSetOf(Permission.of(DataTypes.HEART_RATE, AccessType.READ))

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val granted = store.getGrantedPermissions(permSet)
            if (granted.containsAll(permSet)) {
                _permissionResponse.emit(Pair(AppConstants.SUCCESS, activityId))
            } else {
                requestHeartRatePermission(context, permSet, activityId)
            }
        }
    }

    /**
     * 실제 권한 요청
     */
    private fun requestHeartRatePermission(context: Context, permSet: MutableSet<Permission>, activityId: Int) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val result = store.requestPermissions(permSet, context as Activity)
            Log.d(TAG, "requestPermissions: result = $result")

            if (result.containsAll(permSet)) {
                _permissionResponse.emit(Pair(AppConstants.SUCCESS, activityId))
            } else {
                withContext(Dispatchers.Main) {
                    _permissionResponse.emit(Pair(AppConstants.NO_PERMISSION, -1))
                }
            }
        }
    }

    /**
     * 상태 초기화
     */
    fun resetPermissionResponse() {
        viewModelScope.launch {
            _permissionResponse.emit(Pair(AppConstants.WAITING, -1))
        }
    }

    companion object {
        private const val TAG = "[HealthMainViewModel]"
    }
}
