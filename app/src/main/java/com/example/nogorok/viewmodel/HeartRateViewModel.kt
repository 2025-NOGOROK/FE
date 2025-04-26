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
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.DataTypes
import com.samsung.android.sdk.health.data.request.LocalTimeFilter
import com.samsung.android.sdk.health.data.request.Ordering
import java.time.LocalDateTime
import kotlinx.coroutines.launch

class HeartRateViewModel(private val healthDataStore: HealthDataStore, activity: Activity) :
    ViewModel() {

    private val _exceptionResponse: MutableLiveData<String> = MutableLiveData<String>()
    private val exceptionHandler = getExceptionHandler(activity, _exceptionResponse)
    private val _dailyHeartRate = MutableLiveData<List<HeartRate>>()
    private val hrResultList: MutableList<HeartRate> = mutableListOf()
    val dailyHeartRate: LiveData<List<HeartRate>> = _dailyHeartRate
    val dayStartTimeAsText = ObservableField<String>()
    val exceptionResponse: LiveData<String> = _exceptionResponse

    fun readHeartRateData(dateTime: LocalDateTime) {
        dayStartTimeAsText.set(dateTime.format(dateFormat))

        val localTimeFilter = LocalTimeFilter.of(dateTime, dateTime.plusDays(1))
        val readRequest = DataTypes.HEART_RATE.readDataRequestBuilder
            .setLocalTimeFilter(localTimeFilter)
            .setOrdering(Ordering.DESC)
            .build()

        /**  Make SDK call to read heart rate data */
        viewModelScope.launch(AppConstants.SCOPE_IO_DISPATCHERS + exceptionHandler) {
            val heartRateList = healthDataStore.readData(readRequest).dataList
            processReadDataResponse(heartRateList)
        }
    }

    private fun processReadDataResponse(heartRateList: List<HealthDataPoint>) {
        hrResultList.clear()

        // 00:00 ~ 23:30 까지 총 48개 구간 초기화
        val intervals = mutableListOf<HeartRate>()
        for (i in 0 until 24) {
            val startHour = i
            val endHour = (i + 1) % 24
            val startTime = String.format("%02d:00", startHour)
            val endTime = String.format("%02d:00", endHour)
            intervals.add(HeartRate(1000f, 0f, 0f, startTime, endTime, 0))
        }

        heartRateList.forEach { heartRateData ->
            val time = LocalDateTime.ofInstant(heartRateData.startTime, heartRateData.zoneOffset)
            val hour = time.hour

            var index = hour
            if (index >= 24) {
                index = 23
            }

            if (index in 0 until 24) {
                processHeartRateData(heartRateData, intervals[index])
            }
        }

        // 평균 처리
        intervals.forEach { quarter ->
            if (quarter.count > 0) {
                processAvgData(quarter)
            }
        }

        _dailyHeartRate.postValue(hrResultList)
    }


    data class HeartRate(
        var min: Float,
        var max: Float,
        var avg: Float,
        var startTime: String,
        var endTime: String,
        var count: Int,
        var stress: Float = 0f
    )

    private fun calculateStressIndex(avg: Float): Float {
        return when {
            avg <= 60 -> 10f
            avg in 61f..70f -> 30f
            avg in 71f..80f -> 50f
            avg in 81f..90f -> 70f
            else -> 90f
        }
    }


    private fun processHeartRateData(heartRateData: HealthDataPoint, hrQuarter: HeartRate) {
        hrQuarter.apply {
            heartRateData.getValue(DataType.HeartRateType.HEART_RATE)?.let {
                avg += it
                count++
            }
            heartRateData.getValue(DataType.HeartRateType.MAX_HEART_RATE)?.let {
                max = maxOf(max, it)
            }
            heartRateData.getValue(DataType.HeartRateType.MIN_HEART_RATE)?.let {
                if (min != 0f) {
                    min = minOf(min, it)
                }
            }
        }
    }

    private fun processAvgData(hrQuarter: HeartRate) {
        if (hrQuarter.count != 0) {
            hrQuarter.avg /= hrQuarter.count
            hrQuarter.stress = calculateStressIndex(hrQuarter.avg) // 스트레스 계산 추가
            hrResultList.add(hrQuarter)
        }
    }


    private fun LocalDateTime.isBetween(fromHour: Int, toHour: Int) =
        this >= this.withHour(fromHour).withMinute(0).withSecond(0) &&
                this <= this.withHour(toHour).withMinute(59).withSecond(59)
}
