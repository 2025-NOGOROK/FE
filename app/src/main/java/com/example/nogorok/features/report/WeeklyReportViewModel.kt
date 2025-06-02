// WeeklyReportViewModel.kt
package com.example.nogorok.features.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.WeeklyResponse
import kotlinx.coroutines.launch

class WeeklyReportViewModel : ViewModel() {

    private val _emotionValues = MutableLiveData<List<Int>>()
    val emotionValues: LiveData<List<Int>> = _emotionValues

    fun fetchWeeklyEmotion() {
        viewModelScope.launch {
            try {
                val response: WeeklyResponse = RetrofitClient.weeklyApi.getWeeklyEmotionFatigue()
                val values = response.emotionFatigue.map { mapEmotionToValue(it.emotion) }
                _emotionValues.value = values
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun mapEmotionToValue(emotion: String): Int {
        return when (emotion.uppercase()) {
            "JOY" -> 80
            "NORMAL" -> 50
            "DEPRESSED" -> 30
            "ANGRY" -> 10
            "IRRITATED" -> 20
            else -> 40
        }
    }
}