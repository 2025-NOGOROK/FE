package com.example.nogorok.features.report

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.WeeklyResponse
import com.example.nogorok.network.dto.WeeklyStressResponse
import com.example.nogorok.network.dto.WeeklyWeatherResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException

class WeeklyReportViewModel : ViewModel() {

    // 감정 값 (이모지 그래프용)
    private val _emotionValues = MutableLiveData<List<Int>>()
    val emotionValues: LiveData<List<Int>> = _emotionValues

    private val _emotionNames = MutableLiveData<List<String>>()
    val emotionNames: LiveData<List<String>> = _emotionNames


    // 스트레스 값 (막대 그래프용)
    private val _stressValues = MutableLiveData<List<Int>>()
    val stressValues: LiveData<List<Int>> = _stressValues

    // 날씨 값 (이모지 날씨용)
    private val _weatherValues = MutableLiveData<List<String>>()
    val weatherValues: LiveData<List<String>> = _weatherValues

    fun fetchWeeklyEmotion() {
        viewModelScope.launch {
            try {
                val response: WeeklyResponse = RetrofitClient.weeklyApi.getWeeklyEmotionFatigue()

                val fatigueList = response.days.map { mapFatigueToValue(it.fatigue) }  // null도 포함
                val emotionList = response.days.map { it.emotion ?: "보통" }           // null은 "보통" 처리

                _emotionValues.value = fatigueList
                _emotionNames.value = emotionList
            } catch (e: HttpException) {
                Log.e("WeeklyReportViewModel", "Emotion API error: ${e.code()} ${e.message()}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun fetchWeeklyStress() {
        viewModelScope.launch {
            try {
                val response: WeeklyStressResponse = RetrofitClient.weeklyApi.getWeeklyStress()
                val values = response.days.map { it.averageStress }
                _stressValues.value = values
            } catch (e: HttpException) {
                Log.e("WeeklyReportViewModel", "Stress API error: ${e.code()} ${e.message()}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchWeeklyWeather() {
        viewModelScope.launch {
            try {
                val response: WeeklyWeatherResponse = RetrofitClient.weeklyApi.getWeeklyWeather()
                val values = response.days.map { it.weather }
                _weatherValues.value = values
            } catch (e: HttpException) {
                Log.e("WeeklyReportViewModel", "Weather API error: ${e.code()} ${e.message()}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun mapFatigueToValue(fatigue: String?): Int {
        return when (fatigue?.uppercase()) {
            "에너지 넘침" -> 80
            "보통" -> 50
            "매우 피곤" -> 20
            else -> 0
        }
    }
}
