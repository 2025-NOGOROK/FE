package com.example.nogorok.features.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScheduleViewModel : ViewModel() {

    // 일정 리스트를 LiveData로 관리 (초기값은 빈 리스트)
    private val _scheduleList = MutableLiveData<List<Schedule>>(emptyList())
    val scheduleList: LiveData<List<Schedule>> = _scheduleList

    // 현재 수정 중인 일정(선택된 일정)
    var editingSchedule: Schedule? = null

    // 일정 추가 (가장 최근 일정이 맨 위)
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
