// ScheduleViewModel.kt
package com.example.nogorok.features.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScheduleViewModel : ViewModel() {
    private val _scheduleList = MutableLiveData<MutableList<Schedule>>(mutableListOf())
    val scheduleList: LiveData<MutableList<Schedule>> = _scheduleList

    // 수정/추가용 임시 저장
    var editingSchedule: Schedule? = null

    fun addSchedule(schedule: Schedule) {
        _scheduleList.value?.add(schedule)
        _scheduleList.value = _scheduleList.value // LiveData 갱신
    }

    fun updateSchedule(schedule: Schedule) {
        _scheduleList.value = _scheduleList.value?.map {
            if (it.id == schedule.id) schedule else it
        }?.toMutableList()
    }
}
