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
        val hrOfFirstQuarter = HeartRate(1000f, 0f, 0f, "00:00", "06:00", 0)
        val hrOfSecondQuarter = HeartRate(1000f, 0f, 0f, "06:00", "12:00", 0)
        val hrOfThirdQuarter = HeartRate(1000f, 0f, 0f, "12:00", "18:00", 0)
        val hrOfFourthQuarter = HeartRate(1000f, 0f, 0f, "18:00", "24:00", 0)

        heartRateList.forEach { heartRateData ->
            val time = LocalDateTime.ofInstant(heartRateData.startTime, heartRateData.zoneOffset)
            when {
                time.isBetween(0, 5) -> processHeartRateData(heartRateData, hrOfFirstQuarter)
                time.isBetween(6, 11) -> processHeartRateData(heartRateData, hrOfSecondQuarter)
                time.isBetween(12, 17) -> processHeartRateData(heartRateData, hrOfThirdQuarter)
                time.isBetween(18, 23) -> processHeartRateData(heartRateData, hrOfFourthQuarter)
            }
        }

        processAvgData(hrOfFirstQuarter)
        processAvgData(hrOfSecondQuarter)
        processAvgData(hrOfThirdQuarter)
        processAvgData(hrOfFourthQuarter)

        _dailyHeartRate.postValue(hrResultList)
    }

    data class HeartRate(
        var min: Float,
        var max: Float,
        var avg: Float,
        var startTime: String,
        var endTime: String,
        var count: Int
    )

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
        hrQuarter.apply {
            if (hrQuarter.count != 0) {
                hrQuarter.avg /= hrQuarter.count
                hrResultList.add(hrQuarter)
            }
        }
    }

    private fun LocalDateTime.isBetween(fromHour: Int, toHour: Int) =
        this >= this.withHour(fromHour).withMinute(0).withSecond(0) &&
                this <= this.withHour(toHour).withMinute(59).withSecond(59)
}
