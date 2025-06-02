package com.example.nogorok.features.auth.survey

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SurveyViewModel : ViewModel() {
    var mode: String = "register"

    fun updateMode(mode: String) {
        this.mode = mode
    }

    val scheduleType = MutableLiveData<String?>() // "루즈", "타이트"
    val suddenChangePreferred = MutableLiveData<Boolean?>()
    val chronotype = MutableLiveData<String?>() // "아침", "저녁"
    val preferAlone = MutableLiveData<String?>() // "혼자", "함께"
    val stressReaction = MutableLiveData<String?>() // 예: "감각 민감+추구형"
    val hasStressRelief = MutableLiveData<Boolean?>()
    val stressReliefMethods = MutableLiveData<List<String>?>()

}
