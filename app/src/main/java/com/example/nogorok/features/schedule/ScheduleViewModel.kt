package com.example.nogorok.features.schedule

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.ShortRestResponse
import com.example.nogorok.utils.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ScheduleViewModel : ViewModel() {

    private val _selectedDate = MutableLiveData(LocalDate.now())
    val selectedDate: LiveData<LocalDate> = _selectedDate

    private val _scheduleList = MutableLiveData<List<ScheduleItem>>()
    val scheduleList: LiveData<List<ScheduleItem>> = _scheduleList

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun setShortRestItems(items: List<ShortRestResponse>) {
        val shortRestSchedules = items.map {
            ScheduleItem(
                title = it.title,
                startTime = it.startTime,
                endTime = it.endTime,
                isPinned = false,
                isShortRest = true
            )
        }

        val current = _scheduleList.value.orEmpty()
        _scheduleList.value = current + shortRestSchedules
    }

    fun fetchGoogleEvents(context: Context, date: LocalDate) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = TokenManager.getJwtToken(context)
                if (token != null) {
                    val timeMin = date.atTime(0, 0, 0).toString() + "+09:00"
                    val timeMax = date.atTime(23, 59, 59).toString() + "+09:00"

                    val response = RetrofitClient.calendarApi.getGoogleEvents(
                        jwt = "Bearer $token",
                        timeMin = timeMin,
                        timeMax = timeMax
                    )

                    Log.d("시간 확인", timeMin)
                    Log.d("시간 확인", timeMax)

                    if (response.isSuccessful) {
                        val items = response.body().orEmpty()

                        val formatter = DateTimeFormatter.ofPattern("HH:mm")

                        val scheduleItems = items.map { item ->
                            val start = item.startDateTime.substring(11, 16) // "T10:00"
                            val end = item.endDateTime.substring(11, 16)

                            ScheduleItem(
                                title = item.title,
                                startTime = start,
                                endTime = end,
                                isPinned = false,
                                isShortRest = false
                            )
                        }

                        _scheduleList.postValue(scheduleItems)
                    } else {
                        Log.e("API", "구글 일정 조회 실패: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "구글 일정 조회 중 오류", e)
            }
        }
    }
}
