package com.example.nogorok.features.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nogorok.network.dto.ShortRestResponse
import java.time.LocalDate

class ScheduleViewModel : ViewModel() {

    private val _selectedDate = MutableLiveData(LocalDate.now())
    val selectedDate: LiveData<LocalDate> = _selectedDate

    private val _scheduleList = MutableLiveData<List<ScheduleItem>>()
    val scheduleList: LiveData<List<ScheduleItem>> = _scheduleList

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun loadSchedules(date: LocalDate) {
        // 더미 일정 예시
        _scheduleList.value = listOf(
            ScheduleItem("기존 일정 1", "10:00 - 11:00", isPinned = true),
            ScheduleItem("기존 일정 2", "14:00 - 15:00", isPinned = false)
        )
    }

    fun setShortRestItems(items: List<ShortRestResponse>) {
        val shortRestSchedules = items.map {
            val time = "${it.startTime} - ${it.endTime}"
            ScheduleItem(
                title = it.title,
                time = time,
                isPinned = false, // 서버에서 고정 여부 정보가 없으므로 false 고정
                isShortRest = true
            )
        }

        val current = _scheduleList.value.orEmpty()
        _scheduleList.value = current + shortRestSchedules
    }
}
