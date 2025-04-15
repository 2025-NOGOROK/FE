package com.example.nogorok.model

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.samsung.android.sdk.healthdata.*

class HeartRateViewModel(application: Application) : AndroidViewModel(application) {

    private val _heartRates = MutableLiveData<List<Float>>()
    val heartRates: LiveData<List<Float>> get() = _heartRates

    private lateinit var mStore: HealthDataStore
    private var isConnected = false

    // 연결 상태 전달용 LiveData
    private val _permissionRequired = MutableLiveData<Boolean>()
    val permissionRequired: LiveData<Boolean> get() = _permissionRequired

    fun initHealthStore() {
        mStore = HealthDataStore(getApplication(), mConnectionListener)
        mStore.connectService()
    }

    private val mConnectionListener = object : HealthDataStore.ConnectionListener {
        override fun onConnected() {
            Log.d("HeartRateVM", "HealthDataStore 연결됨")
            isConnected = true
            checkPermissions()
        }

        override fun onConnectionFailed(error: HealthConnectionErrorResult) {
            Log.e("HeartRateVM", "연결 실패: $error")
        }

        override fun onDisconnected() {
            Log.i("HeartRateVM", "연결 해제됨")
            isConnected = false
        }
    }

    private fun checkPermissions() {
        val pmsManager = HealthPermissionManager(mStore)
        val keySet = setOf(
            HealthPermissionManager.PermissionKey(
                HealthConstants.HeartRate.HEALTH_DATA_TYPE,
                HealthPermissionManager.PermissionType.READ
            )
        )

        val result = pmsManager.isPermissionAcquired(keySet)
        if (result.containsValue(false)) {
            Log.w("HeartRateVM", "권한 없음 → Fragment에 알림")
            _permissionRequired.postValue(true) // Fragment에서 requestPermissions 호출해야 함
        } else {
            Log.d("HeartRateVM", "권한 이미 있음 → 데이터 읽기")
            readHeartRate()
        }
    }

    fun requestPermissions(activity: Activity) {
        if (!isConnected) {
            Log.e("HeartRateVM", "HealthDataStore가 연결되지 않음")
            return
        }

        val pmsManager = HealthPermissionManager(mStore)
        val keySet = setOf(
            HealthPermissionManager.PermissionKey(
                HealthConstants.HeartRate.HEALTH_DATA_TYPE,
                HealthPermissionManager.PermissionType.READ
            )
        )

        pmsManager.requestPermissions(keySet, activity)
            .setResultListener { result ->
                if (result.resultMap.containsValue(false)) {
                    Log.e("HeartRateVM", "권한 거부됨")
                } else {
                    Log.d("HeartRateVM", "권한 획득 → 데이터 읽기")
                    readHeartRate()
                }
            }
    }

    private fun readHeartRate() {
        val resolver = HealthDataResolver(mStore, null)
        val request = HealthDataResolver.ReadRequest.Builder()
            .setDataType(HealthConstants.HeartRate.HEALTH_DATA_TYPE)
            .build()

        resolver.read(request).setResultListener { result ->
            val values = mutableListOf<Float>()
            result.forEach {
                values.add(it.getFloat(HealthConstants.HeartRate.HEART_RATE))
            }
            _heartRates.postValue(values)
            result.close()
        }
    }
}
