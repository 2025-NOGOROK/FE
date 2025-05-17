/*
 * Copyright (C) 2024 Samsung Electronics Co., Ltd. All rights reserved
 */
package com.example.nogorok.features.connect.health.viewmodel

import android.app.Activity
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nogorok.features.connect.health.utils.AppConstants
import com.example.nogorok.features.connect.health.utils.dateFormat
import com.example.nogorok.features.connect.health.utils.getExceptionHandler
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.AssociatedDataPoints
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.DataTypes
import com.samsung.android.sdk.health.data.request.IdFilter
import com.samsung.android.sdk.health.data.request.LocalTimeFilter
import com.samsung.android.sdk.health.data.request.Ordering
import java.time.LocalDateTime
import kotlinx.coroutines.launch

class SleepViewModel(private val healthDataStore: HealthDataStore, activity: Activity) :
    ViewModel() {

    private val _exceptionResponse: MutableLiveData<String> = MutableLiveData<String>()
    private val _dailySleepData = MutableLiveData<List<HealthDataPoint>>()
    private val _associatedData = MutableLiveData<List<AssociatedDataPoints>>()
    private val exceptionHandler = getExceptionHandler(activity, _exceptionResponse)
    val dayStartTimeAsText = ObservableField<String>()
    val dailySleepData: LiveData<List<HealthDataPoint>> = _dailySleepData
    val associatedData: LiveData<List<AssociatedDataPoints>> = _associatedData
    val exceptionResponse: LiveData<String> = _exceptionResponse

    fun readSleepData(dateTime: LocalDateTime) {
        dayStartTimeAsText.set(dateTime.format(dateFormat))

        val readRequest = DataTypes.SLEEP.readDataRequestBuilder
            .setLocalTimeFilter(LocalTimeFilter.of(dateTime, dateTime.plusDays(1)))
            .setOrdering(Ordering.ASC)
            .build()

        /**  Make SDK call to read Sleep data */
        viewModelScope.launch(AppConstants.SCOPE_IO_DISPATCHERS + exceptionHandler) {
            val sleepDataList = healthDataStore.readData(readRequest).dataList
            _dailySleepData.postValue(sleepDataList)

            if (sleepDataList.isNotEmpty()) {
                val ids = IdFilter.builder()
                sleepDataList.forEach { sleep ->
                    ids.addDataUid(sleep.uid)
                }
                readAssociatedData(ids.build())
            }
        }
    }

    private fun readAssociatedData(idFilter: IdFilter) {
        val associatedReadRequest = DataTypes.SLEEP.associatedReadRequestBuilder
            .setIdFilter(idFilter)
            .addAssociatedDataType(DataType.SleepType.Associates.SKIN_TEMPERATURE)
            .addAssociatedDataType(DataType.SleepType.Associates.BLOOD_OXYGEN)
            .build()

        /**  Make SDK call to read sleep associated data */
        viewModelScope.launch(AppConstants.SCOPE_IO_DISPATCHERS + exceptionHandler) {
            val associatedList = healthDataStore.readAssociatedData(associatedReadRequest).dataList
            _associatedData.postValue(associatedList)
        }
    }
}
