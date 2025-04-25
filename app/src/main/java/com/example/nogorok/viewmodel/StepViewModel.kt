/*
 * Copyright (C) 2024 Samsung Electronics Co., Ltd. All rights reserved
 */
package com.example.nogorok.viewmodel

import android.app.Activity
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nogorok.utils.AppConstants
import com.example.nogorok.utils.dateFormat
import com.example.nogorok.utils.getExceptionHandler
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.AggregatedData
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.LocalTimeFilter
import com.samsung.android.sdk.health.data.request.LocalTimeGroup
import com.samsung.android.sdk.health.data.request.LocalTimeGroupUnit
import com.samsung.android.sdk.health.data.request.Ordering
import com.samsung.android.sdk.health.data.response.DataResponse
import java.time.LocalDateTime
import kotlinx.coroutines.launch

class StepViewModel(private val healthDataStore: HealthDataStore, activity: Activity) :
    ViewModel() {

    private val _exceptionResponse: MutableLiveData<String> = MutableLiveData<String>()
    private val _totalStepCountData = MutableLiveData<List<AggregatedData<Long>>>()
    private val exceptionHandler = getExceptionHandler(activity, _exceptionResponse)
    val totalStepCountData: LiveData<List<AggregatedData<Long>>> = _totalStepCountData
    val exceptionResponse: LiveData<String> = _exceptionResponse
    val totalStepCount = ObservableField<String>()
    val dayStartTimeAsText = ObservableField<String>()

    fun readStepData(dateTime: LocalDateTime) {
        dayStartTimeAsText.set(dateTime.format(dateFormat))

        val localtimeFilter = LocalTimeFilter.of(dateTime, dateTime.plusDays(1))
        val localTimeGroup = LocalTimeGroup.of(LocalTimeGroupUnit.HOURLY, 1)
        val aggregateRequest = DataType.StepsType.TOTAL.requestBuilder
            .setLocalTimeFilterWithGroup(localtimeFilter, localTimeGroup)
            .setOrdering(Ordering.ASC)
            .build()

        /**  Make SDK call to read step data */
        viewModelScope.launch(AppConstants.SCOPE_IO_DISPATCHERS + exceptionHandler) {
            val result = healthDataStore.aggregateData(aggregateRequest)
            processAggregateDataResponse(result)
        }
    }

    private fun processAggregateDataResponse(
        result: DataResponse<AggregatedData<Long>>
    ) {
        val stepCount = ArrayList<AggregatedData<Long>>()
        var totalSteps: Long = 0

        result.dataList.forEach { stepData ->
            val hourlySteps = stepData.value as Long
            totalSteps += hourlySteps
            stepCount.add(stepData)
        }
        totalStepCount.set(totalSteps.toString())
        _totalStepCountData.postValue(stepCount)
    }
}
