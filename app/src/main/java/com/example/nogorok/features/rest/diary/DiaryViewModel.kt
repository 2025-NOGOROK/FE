package com.example.nogorok.features.rest.diary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.DiaryRequest
import com.example.nogorok.network.dto.DiaryResponse
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DiaryViewModel : ViewModel() {

    // 하루기록 저장
    fun submitHaru(
        emotion: String,
        fatigueLevel: String,
        weather: String,
        specialNotes: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit = {}
    ) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())

        val request = DiaryRequest(
            date = today,
            emotion = emotion,
            fatigueLevel = fatigueLevel,
            weather = weather,
            sleepHours = 0,
            specialNotes = specialNotes
        )

        viewModelScope.launch {
            try {
                val response = RetrofitClient.diaryApi.postDiary(request)
                if (response.isSuccessful) {
                    Log.d("DiaryViewModel", "하루기록 저장 성공")
                    onSuccess()
                } else {
                    Log.e("DiaryViewModel", "저장 실패: ${response.code()} ${response.errorBody()?.string()}")
                    onError(Exception("저장 실패"))
                }
            } catch (e: Exception) {
                Log.e("DiaryViewModel", "네트워크 에러", e)
                onError(e)
            }
        }
    }

    // 하루기록 조회
    fun fetchHaru(
        date: String,
        onResult: (DiaryResponse?) -> Unit,
        onError: (Throwable) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.diaryApi.getDiary(date)
                if (response.isSuccessful) {
                    val diary = response.body()
                    onResult(diary)
                } else {
                    onResult(null)
                }
            } catch (e: Exception) {
                Log.e("DiaryViewModel", "조회 실패", e)
                onError(e)
            }
        }
    }

}
