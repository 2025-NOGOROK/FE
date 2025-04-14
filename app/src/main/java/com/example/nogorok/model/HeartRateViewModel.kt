package com.example.nogorok.model

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

    fun initHealthStore() {
        mStore = HealthDataStore(getApplication(), mConnectionListener)
        mStore.connectService()
    }

    private val mConnectionListener = object : HealthDataStore.ConnectionListener {
        override fun onConnected() {
            requestPermissionAndRead()
        }

        override fun onConnectionFailed(error: HealthConnectionErrorResult) {
            Log.e("HeartRateVM", "연결 실패: $error")
        }

        override fun onDisconnected() {
            Log.i("HeartRateVM", "연결 해제됨")
        }
    }

    fun requestPermissions(activity: android.app.Activity) {
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
                    readHeartRate()
                }
            }
    }

    private fun requestPermissionAndRead() {
        val pmsManager = HealthPermissionManager(mStore)
        val keySet = setOf(
            HealthPermissionManager.PermissionKey(
                HealthConstants.HeartRate.HEALTH_DATA_TYPE,
                HealthPermissionManager.PermissionType.READ
            )
        )

        if (pmsManager.isPermissionAcquired(keySet).containsValue(false)) {
            Log.w("HeartRateVM", "권한 없음 → Fragment에서 요청 필요")
        } else {
            readHeartRate()
        }
    }

    fun readHeartRate() {
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
