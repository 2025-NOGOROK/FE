package com.example.nogorok.features.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.DailyStressEntry
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MonthlyReportViewModel : ViewModel() {

    private val _emotionData = MutableLiveData<Map<String, Double>>()
    val emotionData: LiveData<Map<String, Double>> = _emotionData

    private val _dailyStressList = MutableLiveData<List<DailyStressEntry>>()
    val dailyStressList: LiveData<List<DailyStressEntry>> = _dailyStressList

    fun fetchEmotionRatio(year: Int = LocalDate.now().year, month: Int = LocalDate.now().monthValue) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.monthlyApi.getMonthlyEmotion(year, month)
                _emotionData.postValue(response.emotionPercent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchMonthlyStress(year: Int = LocalDate.now().year, month: Int = LocalDate.now().monthValue) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.monthlyApi.getMonthlyStress(year, month)
                _dailyStressList.postValue(response.dailyStressList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
