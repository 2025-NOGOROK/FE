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

    private val _lawtimes = MutableLiveData<String>()
    val lawtimes: LiveData<String> = _lawtimes

    fun fetchLatestStress() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.homeApi.getLatestStress()
                Log.d("fetchLatestStress", "response code: ${response.code()}, body: ${response.body()}")

                if (response.isSuccessful) {
                    _stress.value = response.body()?.avg ?: 0f
                } else {
                    Log.e("fetchLatestStress", "API 실패: ${response.code()} - ${response.errorBody()?.string()}")
                    _stress.value = 0f
                }
            } catch (e: Exception) {
                Log.e("fetchLatestStress", "예외 발생: ${e.message}", e)
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
                    val content = response.body()?.string() ?: "내용 없음"
                    _trauma.value = content
                    Log.d("HomeViewModel", "기사 내용: $content")

                    // 🔍 이미지 URL 추출 (.png, .jpg 등)
                    val imageRegex = Regex("https?://[^\\s'\"]+\\.(png|jpg|jpeg|gif)")
                    val firstImage = imageRegex.find(content)?.value

                    if (firstImage != null) {
                        _trauma.value = firstImage ?: ""
                        Log.d("HomeViewModel", "첫 번째 이미지 URL: $firstImage")
                    } else {
                        _trauma.value = ""
                        Log.w("HomeViewModel", "이미지 URL을 찾지 못했습니다.")
                    }
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
                    val content = response.body()?.string() ?: "내용 없음"
                    _samsungStress.value = content
                    Log.d("HomeViewModel", "기사 내용: $content")

                    // 🔍 이미지 URL 추출 (.png, .jpg 등)
                    val imageRegex = Regex("https?://[^\\s'\"]+\\.(png|jpg|jpeg|gif)")
                    val firstImage = imageRegex.find(content)?.value

                    if (firstImage != null) {
                        _samsungStress.value = firstImage ?: ""
                        Log.d("HomeViewModel", "첫 번째 이미지 URL: $firstImage")
                    } else {
                        _samsungStress.value = ""
                        Log.w("HomeViewModel", "이미지 URL을 찾지 못했습니다.")
                    }
                } else {
                    Log.e("HomeViewModel", "크롤링 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "크롤링 예외: ${e.message}")
            }
        }
    }


    fun fetchLawTimes() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.homeApi.getLawtimesArticle()
                if (response.isSuccessful) {
                    val content = response.body()?.string() ?: "내용 없음"
                    _lawtimes.value = content
                    Log.d("HomeViewModel", "기사 내용: $content")

                    // 🔍 이미지 URL 추출 (.png, .jpg 등)
                    val imageRegex = Regex("https?://[^\\s'\"]+\\.(png|jpg|jpeg|gif)")
                    val firstImage = imageRegex.find(content)?.value

                    if (firstImage != null) {
                        _lawtimes.value = firstImage ?: ""
                        Log.d("HomeViewModel", "첫 번째 이미지 URL: $firstImage")
                    } else {
                        _lawtimes.value = ""
                        Log.w("HomeViewModel", "이미지 URL을 찾지 못했습니다.")
                    }
                } else {
                    Log.e("HomeViewModel", "크롤링 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "크롤링 예외: ${e.message}")
            }
        }
    }

}
