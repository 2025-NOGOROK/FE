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
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


class ScheduleViewModel : ViewModel() {

    private val _selectedDate = MutableLiveData(LocalDate.now())
    val selectedDate: LiveData<LocalDate> = _selectedDate

    private val _scheduleList = MutableLiveData<List<ScheduleItem>>()
    val scheduleList: LiveData<List<ScheduleItem>> = _scheduleList

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun setShortRestItems(items: List<ShortRestResponse>, context: Context) {
        val shortRestSchedules = items.map {
            ScheduleItem(
                title = it.title,
                startTime = it.startTime.substring(11, 16),
                endTime = it.endTime.substring(11, 16),
                isPinned = false,
                sourceType = it.sourceType
            )
        }

        // ❗ UI에는 표시하지 않음, 로그로만 찍기
        shortRestSchedules.forEach {
            Log.d("ShortRestItem", "title=${it.title}, time=${it.startTime}-${it.endTime}, sourceType=${it.sourceType}")
        }

        // ✅ 호출 성공 후 구글 이벤트 가져오기 실행
        fetchGoogleEvents(context, selectedDate.value ?: LocalDate.now())
    }


    fun fetchGoogleEvents(context: Context, date: LocalDate) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = TokenManager.getAccessToken(context)
                if (token != null) {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
                    val timeMin = date.atTime(0, 0).atOffset(ZoneOffset.ofHours(9)).format(formatter)
                    val timeMax = date.atTime(23, 0).atOffset(ZoneOffset.ofHours(9)).format(formatter)

                    val response = RetrofitClient.calendarApi.getGoogleEvents(
                        timeMin = timeMin,
                        timeMax = timeMax
                    )

                    Log.d("시간 확인", timeMin)
                    Log.d("시간 확인", timeMax)

                    if (response.isSuccessful) {
                        val items = response.body().orEmpty()
                        val formatterHHmm = DateTimeFormatter.ofPattern("HH:mm")

                        val scheduleItems = items.map { item ->
                            val start = item.startDateTime.substring(11, 16)
                            val end = item.endDateTime.substring(11, 16)

                            ScheduleItem(
                                title = item.title,
                                startTime = start,
                                endTime = end,
                                isPinned = false,
                                sourceType = item.sourceType
                            )

                        }

                        Log.d("Schedule", "API 호출됨: ${scheduleItems.size}개 이벤트 수신")

                        _scheduleList.postValue(scheduleItems)
                    } else {
                        Log.e("API", "구글 일정 조회 실패: ${response.code()} / ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "구글 일정 조회 중 오류", e)
            }
        }
    }

}
