package com.example.nogorok.features.auth.survey

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SurveyViewModel : ViewModel() {

    // Step1: 루즈 or 타이트 일정 선호
    val scheduleType = MutableLiveData<String?>()

    // Step2: 갑작스런 일정 변경 선호 여부
    val suddenChangePreferred = MutableLiveData<Boolean?>()

    // Step3: 아침형 or 저녁형 인간
    val chronotype = MutableLiveData<String?>() // "morning", "evening"

    // Step4: 혼자 vs 함께
    val preferAlone = MutableLiveData<String?>() // "alone", "together"

    // Step5: 스트레스 반응 (문자열로 저장)
    val stressReaction = MutableLiveData<String?>()

    // Step6: 스트레스 해소 방법 유무
    val hasStressRelief = MutableLiveData<Boolean?>()

    // Step7: 스트레스 해소 방법 3가지
    val stressReliefMethods = MutableLiveData<List<String>?>()

    /**
     * 전체 데이터를 서버에 보낼 수 있도록 하나의 Map 또는 데이터 객체로 구성해 반환할 수도 있습니다.
     */
    fun toSurveyRequestBody(): Map<String, Any?> {
        return mapOf(
            "scheduleType" to scheduleType.value,
            "suddenChangePreferred" to suddenChangePreferred.value,
            "chronotype" to chronotype.value,
            "preferAlone" to preferAlone.value,
            "stressReaction" to stressReaction.value,
            "hasStressRelief" to hasStressRelief.value,
            "stressReliefMethods" to stressReliefMethods.value
        )
    }
}
