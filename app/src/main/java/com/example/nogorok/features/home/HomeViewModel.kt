package com.example.nogorok.features.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.HomeResponse
import com.example.nogorok.network.dto.TourResponse
import com.example.nogorok.network.dto.TourItem
import android.util.Log
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _stress = MutableLiveData<Float>()
    val stress: LiveData<Float> = _stress

    private val _tourList = MutableLiveData<List<TourItem>>() // ✅ 추가
    val tourList: LiveData<List<TourItem>> = _tourList        // ✅ 추가

    private val _trauma = MutableLiveData<String>()
    val trauma: LiveData<String> = _trauma

    private val _samsungStress = MutableLiveData<String>()
    val samsungStress: LiveData<String> = _samsungStress

    fun fetchLatestStress() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.homeApi.getLatestStress()
                if (response.isSuccessful) {
                    _stress.value = response.body()?.stress ?: 0f
                } else {
                    _stress.value = 0f
                }
            } catch (e: Exception) {
                _stress.value = 0f
            }
        }
    }

    fun fetchTourByLocation(x: Double, y: Double) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.homeApi.getTourByLocation(mapX = x, mapY = y)
                if (response.isSuccessful) {
                    val tourList = response.body()?.response?.body?.items?.item ?: emptyList()
                    _tourList.value = tourList // ✅ LiveData에 할당
                    Log.d("HomeViewModel", "받은 투어 리스트: $tourList")
                } else {
                    Log.e("HomeViewModel", "투어 조회 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "투어 조회 중 예외 발생", e)
            }
        }
    }

    fun fetchTraumaArticle() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.homeApi.getTraumaArticle()
                if (response.isSuccessful) {
                    _trauma.value = response.body()?.content ?: "내용 없음"
                    Log.d("HomeViewModel", "기사 내용: ${response.body()?.content}")
                } else {
                    Log.e("HomeViewModel", "크롤링 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "크롤링 예외: ${e.message}")
            }
        }
    }

    fun fetchSamsungStress() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.homeApi.getSamsungStress()
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("SamsungStressRaw", "🔵 응답 본문: $body")
                } else {
                    Log.e("SamsungStressRaw", "🔴 실패 코드: ${response.code()} / ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("SamsungStressRaw", "🔥 예외 발생: ${e.message}", e)
            }
        }
    }

}
