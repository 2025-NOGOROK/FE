package com.example.nogorok.model

import android.app.Activity
import androidx.lifecycle.*
import com.example.nogorok.utils.AppConstants
import com.example.nogorok.utils.getExceptionHandler
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.DataTypes
import com.samsung.android.sdk.health.data.request.LocalTimeFilter
import com.samsung.android.sdk.health.data.request.Ordering
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class HeartRateViewModel(
    private val healthDataStore: HealthDataStore,
    activity: Activity
) : ViewModel() {

    private val _exceptionResponse = MutableLiveData<String>()
    val exceptionResponse: LiveData<String> = _exceptionResponse

    private val _dailyHeartRate = MutableLiveData<List<HeartRate>>()
    val dailyHeartRate: LiveData<List<HeartRate>> = _dailyHeartRate

    private val exceptionHandler = getExceptionHandler(activity, _exceptionResponse)
    private val hrResultList = mutableListOf<HeartRate>()

    fun readHeartRateData() {
        val today = LocalDate.now()
        val startDateTime = today.atStartOfDay()
        val endDateTime = startDateTime.plusDays(1)

        val readRequest = DataTypes.HEART_RATE.readDataRequestBuilder
            .setLocalTimeFilter(LocalTimeFilter.of(startDateTime, endDateTime))
            .setOrdering(Ordering.DESC)
            .build()

        viewModelScope.launch(exceptionHandler) {
            val result = healthDataStore.readData(readRequest).dataList
            processReadDataResponse(result)
        }
    }

    private fun processReadDataResponse(dataList: List<HealthDataPoint>) {
        hrResultList.clear()

        val quarters = listOf(
            HeartRate(1000f, 0f, 0f, "00:00", "06:00", 0),
            HeartRate(1000f, 0f, 0f, "06:00", "12:00", 0),
            HeartRate(1000f, 0f, 0f, "12:00", "18:00", 0),
            HeartRate(1000f, 0f, 0f, "18:00", "24:00", 0)
        )

        for (point in dataList) {
            val time = LocalDateTime.ofInstant(point.startTime, point.zoneOffset)
            when (time.hour) {
                in 0..5 -> processHeartRate(point, quarters[0])
                in 6..11 -> processHeartRate(point, quarters[1])
                in 12..17 -> processHeartRate(point, quarters[2])
                in 18..23 -> processHeartRate(point, quarters[3])
            }
        }

        quarters.forEach { quarter ->
            if (quarter.count > 0) {
                quarter.avg /= quarter.count
                hrResultList.add(quarter)
            }
        }

        _dailyHeartRate.postValue(hrResultList)
    }

    private fun processHeartRate(point: HealthDataPoint, quarter: HeartRate) {
        point.getValue(DataType.HeartRateType.HEART_RATE)?.let {
            quarter.avg += it
            quarter.count++
        }
        point.getValue(DataType.HeartRateType.MAX_HEART_RATE)?.let {
            quarter.max = maxOf(quarter.max, it)
        }
        point.getValue(DataType.HeartRateType.MIN_HEART_RATE)?.let {
            if (quarter.min != 0f) {
                quarter.min = minOf(quarter.min, it)
            }
        }
    }

    data class HeartRate(
        var min: Float,
        var max: Float,
        var avg: Float,
        val startTime: String,
        val endTime: String,
        var count: Int
    )
}
