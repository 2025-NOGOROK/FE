package com.example.nogorok.features.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScheduleViewModel : ViewModel() {
    private val _scheduleList = MutableLiveData<List<Schedule>>(emptyList())
    val scheduleList: LiveData<List<Schedule>> = _scheduleList

    var editingSchedule: Schedule? = null

    fun addSchedule(schedule: Schedule) {
        _scheduleList.value = _scheduleList.value.orEmpty() + schedule
    }
}
