package com.example.nogorok.features.connect.health.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.example.nogorok.features.connect.health.utils.AppConstants
import com.example.nogorok.features.connect.health.utils.dateFormat
import com.example.nogorok.features.connect.health.utils.getExceptionHandler
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.HeartRateUploadRequest
import com.example.nogorok.utils.TokenManager
import com.google.gson.Gson
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime

class HeartRateViewModel(private val healthDataStore: HealthDataStore, private val activity: Activity) :
    ViewModel() {

    private val _exceptionResponse: MutableLiveData<String> = MutableLiveData()
    private val exceptionHandler = getExceptionHandler(activity, _exceptionResponse)
    private val _dailyHeartRate = MutableLiveData<List<HeartRate>>()
    private val hrResultList: MutableList<HeartRate> = mutableListOf()
    val dailyHeartRate: LiveData<List<HeartRate>> = _dailyHeartRate
    val dayStartTimeAsText = ObservableField<String>()
    val exceptionResponse: LiveData<String> = _exceptionResponse

    suspend fun readHeartRateData(dateTime: LocalDateTime) {
        dayStartTimeAsText.set(dateTime.format(dateFormat))

        val localTimeFilter = LocalTimeFilter.of(dateTime, dateTime.plusDays(1))
        val readRequest = DataTypes.HEART_RATE.readDataRequestBuilder
            .setLocalTimeFilter(localTimeFilter)
            .setOrdering(Ordering.DESC)
            .build()

        val heartRateList = withContext(AppConstants.SCOPE_IO_DISPATCHERS + exceptionHandler) {
            healthDataStore.readData(readRequest).dataList
        }

        Log.d("HeartRateViewModel", "[${dateTime.toLocalDate()}] Fetched ${heartRateList.size} records")
        processReadDataResponse(heartRateList, dateTime.toLocalDate())
    }

    suspend fun readAllHeartRateDataFrom(startDate: LocalDate, endDate: LocalDate) {
        var current = startDate
        while (!current.isAfter(endDate)) {
            readHeartRateData(current.atStartOfDay())
            current = current.plusDays(1)
        }
    }

    private suspend fun processReadDataResponse(heartRateList: List<HealthDataPoint>, date: LocalDate) {
        hrResultList.clear()

        val intervals = mutableListOf<HeartRate>()
        val hourlyValues = Array(24) { mutableListOf<Float>() }

        for (i in 0 until 24) {
            val startTime = String.format("%02d:00", i)
            val endTime = String.format("%02d:00", (i + 1) % 24)
            intervals.add(HeartRate(1000f, 0f, 0f, startTime, endTime, 0))
        }

        heartRateList.forEach { heartRateData ->
            val time = LocalDateTime.ofInstant(heartRateData.startTime, heartRateData.zoneOffset)
            val hour = time.hour.coerceAtMost(23)
            val value = heartRateData.getValue(DataType.HeartRateType.HEART_RATE)
            if (value != null) hourlyValues[hour].add(value)
            processHeartRateData(heartRateData, intervals[hour])
        }

        intervals.forEachIndexed { index, quarter ->
            if (quarter.count > 0) {
                processAvgData(quarter, date, hourlyValues[index])
            }
        }

        Log.d("HeartRateViewModel", "=== [$date] Heart Rate Summary ===")
        hrResultList.forEach {
            Log.d("HeartRateViewModel", "${it.startTime}~${it.endTime} | min: ${it.min}, max: ${it.max}, avg: ${it.avg}, count: ${it.count}, stress: ${it.stress}")
            sendToServer(it, date)
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

    private fun calculateStressIndex(min: Float, avg: Float, values: List<Float>): Float {
        if (min == 0f || values.isEmpty()) return 0f

        val baseStress = ((avg - 40f) / 50f).coerceIn(0f, 1f) * 100f
        val deviationRatio = ((avg - min) / min).coerceAtLeast(0f)

        var spikeCount = 0
        for (i in 1 until values.size) {
            if (kotlin.math.abs(values[i] - values[i - 1]) > 10f) spikeCount++
        }
        val variabilityRatio = spikeCount / values.size.toFloat()

        return (baseStress * 0.5f + deviationRatio * 100f * 0.35f + variabilityRatio * 100f * 0.15f)
            .coerceIn(0f, 100f)
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
                if (min != 0f) min = minOf(min, it)
            }
        }
    }

    private fun processAvgData(hrQuarter: HeartRate, date: LocalDate, values: List<Float>) {
        if (hrQuarter.count != 0) {
            hrQuarter.avg /= hrQuarter.count
            hrQuarter.stress = calculateStressIndex(hrQuarter.min, hrQuarter.avg, values)
            hrResultList.add(hrQuarter)
        }
    }

    private suspend fun sendToServer(hr: HeartRate, date: LocalDate) {
        val userEmail = TokenManager.getEmail(activity)
        val startTime = "${date}T${hr.startTime}:00"
        val endTime = "${date}T${hr.endTime}:00"

        val request = HeartRateUploadRequest(
            email = userEmail ?: "",
            min = hr.min,
            max = hr.max,
            avg = hr.avg,
            startTime = startTime,
            endTime = endTime,
            count = hr.count,
            stress = hr.stress
        )

        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.healthApi.uploadHeartRate(request)
                if (response.isSuccessful) {
                    Log.d("HeartRateViewModel", "전송 성공: $startTime")
                } else {
                    Log.e("HeartRateViewModel", "전송 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HeartRateViewModel", "전송 에러: ${e.message}")
            }
        }
    }

    fun getLatestStoredStress(context: Context): Float? {
        val prefs = context.getSharedPreferences("HeartRateStorage", Context.MODE_PRIVATE)
        val json = prefs.getString("unsentHeartRates", null) ?: return null
        val type = object : com.google.gson.reflect.TypeToken<List<HeartRate>>() {}.type
        val list: List<HeartRate> = Gson().fromJson(json, type)
        return list.maxByOrNull { it.startTime }?.stress
    }

    private fun saveToLocalStorage(context: Context, heartRateList: List<HeartRate>) {
        val prefs = context.getSharedPreferences("HeartRateStorage", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(heartRateList)
        editor.putString("unsentHeartRates", json)
        editor.apply()
    }
}
