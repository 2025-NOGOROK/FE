package com.example.nogorok.features.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScheduleViewModel : ViewModel() {
    private val _scheduleList = MutableLiveData<List<Schedule>>(emptyList())
    val scheduleList: LiveData<List<Schedule>> = _scheduleList

    var editingSchedule: Schedule? = null

    // 최신순(가장 최근 추가한 일정이 위로)
    fun addSchedule(schedule: Schedule) {
        _scheduleList.value = listOf(schedule) + _scheduleList.value.orEmpty()
    }

    // 일정 수정 (기존 일정 덮어쓰기)
    fun updateSchedule(old: Schedule, new: Schedule) {
        _scheduleList.value = _scheduleList.value.orEmpty().map {
            if (it == old) new else it
        }
        editingSchedule = null
    }
}
