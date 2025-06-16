package com.example.nogorok.features.rest.longrest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.ChoiceRequest
import com.example.nogorok.network.dto.LongRestEventItem
import com.example.nogorok.network.dto.LongRestResponse
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class LongRestViewModel : ViewModel() {

    private val _scenarioItems = MutableLiveData<List<List<RestItem>>>()
    val scenarioItems: LiveData<List<List<RestItem>>> = _scenarioItems

    // 전체 응답 저장용 (시나리오 선택용)
    private var lastRawResponse: List<LongRestResponse>? = null

    private var hasSelectedBefore: Boolean = false

    fun fetchLongRestItems(
        latitude: Double? = null,
        longitude: Double? = null,
        onError: (Throwable) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                Log.d("LongRestViewModel", "hasSelectedBefore=$hasSelectedBefore, lat=$latitude, lng=$longitude")
                val response = if (hasSelectedBefore && latitude != null && longitude != null) {
                    RetrofitClient.longrestApi.getLongRestRecommendationsWithLocation(latitude, longitude)
                } else {
                    RetrofitClient.longrestApi.getLongRestRecommendations()
                }

                if (response.isSuccessful) {
                    val body = response.body() ?: emptyList()
                    lastRawResponse = body

                    val grouped: MutableList<List<RestItem>> = mutableListOf()

                    val calendarItems = body
                        .filter { it.type == "calendar" }
                        .flatMap { it.data }
                        .mapNotNull { event ->
                            RestItem(
                                title = event.title ?: "",
                                description = event.description ?: "",
                                time = "${parseToTime(event.startDateTime)} - ${parseToTime(event.endDateTime)}"
                            )
                        }
                    grouped.add(calendarItems)

                    val eventItems = body.filter { it.type == "event" }
                    eventItems.take(2).forEach { responseItem ->
                        val items = responseItem.data.mapNotNull { event ->
                            RestItem(
                                title = event.title ?: "",
                                description = event.description ?: "",
                                time = "${parseToTime(event.startDateTime)} - ${parseToTime(event.endDateTime)}"
                            )
                        }
                        grouped.add(items)
                    }

                    while (grouped.size < 3) grouped.add(emptyList())

                    _scenarioItems.value = grouped
                } else {
                    onError(Exception("응답 실패: ${response.code()}"))
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    private fun parseToTime(isoString: String?): String {
        return try {
            if (isoString.isNullOrEmpty()) return ""
            val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            OffsetDateTime.parse(isoString, formatter).toLocalTime()
                .format(DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e: Exception) {
            ""
        }
    }

    fun getScenarioItems(index: Int): List<RestItem> {
        return _scenarioItems.value?.getOrNull(index) ?: emptyList()
    }

    fun postSelectedScenario(
        index: Int,
        onSuccess: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        val scenarioList = _scenarioItems.value ?: return
        val selectedGroup = scenarioList.getOrNull(index) ?: return

        val responseForLabel = when (index) {
            0 -> lastRawResponse?.find { it.type == "calendar" }
            1, 2 -> lastRawResponse?.filter { it.type == "event" }?.getOrNull(index - 1)
            else -> null
        } ?: run {
            onError(Exception("선택된 시나리오 응답이 없습니다."))
            return
        }

        val data = selectedGroup.map {
            LongRestEventItem(
                title = it.title,
                description = it.description,
                startTime = null,
                endTime = null,
                startDateTime = null,
                endDateTime = null
            )
        }

        val first = selectedGroup.firstOrNull()
        val start = first?.time?.split(" - ")?.getOrNull(0) ?: ""
        val end = first?.time?.split(" - ")?.getOrNull(1) ?: ""

        val request = ChoiceRequest(
            type = responseForLabel.type,
            label = responseForLabel.label,
            data = data,
            startTime = start,
            endTime = end
        )

        viewModelScope.launch {
            try {
                val res = RetrofitClient.longrestApi.postScenarioChoice(request)
                if (res.isSuccessful) {
                    hasSelectedBefore = true
                    onSuccess()
                } else {
                    onError(Exception("응답 실패: ${res.code()}"))
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}
