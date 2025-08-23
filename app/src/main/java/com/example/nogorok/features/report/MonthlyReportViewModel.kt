package com.example.nogorok.features.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.MonthlyCountsDto
import com.example.nogorok.network.dto.MonthlyStressDto
import com.example.nogorok.network.dto.StressfulDay
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate

class MonthlyReportViewModel : ViewModel() {

    private val _counts = MutableLiveData<MonthlyCountsDto?>()
    val counts: LiveData<MonthlyCountsDto?> = _counts

    private val _stress = MutableLiveData<MonthlyStressDto?>()
    val stress: LiveData<MonthlyStressDto?> = _stress

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /** 현재 연월 기준으로 두 API 동시 조회 */
    fun load(year: Int = LocalDate.now().year, month: Int = LocalDate.now().monthValue) {
        viewModelScope.launch {
            try {
                val countsDefer = async { RetrofitClient.monthlyApi.getMonthly(year, month) }
                val stressDefer = async { RetrofitClient.monthlyApi.getMonthlyStress(year, month) }

                // /api/report/monthly
                countsDefer.await().let { res ->
                    if (res.isSuccessful) {
                        _counts.postValue(res.body())
                    } else {
                        _counts.postValue(null)
                        _error.postValue("monthly 실패: ${res.code()}")
                    }
                }

                // /api/report/monthly-stress
                stressDefer.await().let { res ->
                    if (res.isSuccessful) {
                        _stress.postValue(res.body())
                    } else {
                        _stress.postValue(null)
                        _error.postValue("monthly-stress 실패: ${res.code()}")
                    }
                }
            } catch (t: Throwable) {
                _error.postValue("네트워크 오류: ${t.message}")
                _counts.postValue(null)
                _stress.postValue(null)
            }
        }
    }

    /** 평균 쉼표 개수(월) 계산: (짧은+긴) / 그 달의 일수 */
    fun avgCommaPerDayFor(year: Int, month: Int, counts: MonthlyCountsDto?): String {
        if (counts == null) return "-"
        val days = LocalDate.of(year, month, 1).lengthOfMonth().coerceAtLeast(1)
        val avg = (counts.shortCount + counts.longCount).toDouble() / days
        return String.format("%.1f개", avg)
    }

    /** 가장 스트레스 많았던 날 객체 */
    fun mostStressfulDay(): StressfulDay? = _stress.value?.mostStressful

    /**
     * 서버 스키마에 `leastStressful`이 있는 경우 사용할 수 있도록 안전 래퍼 제공.
     * DTO에 필드가 없더라도 null 반환하도록 try-catch 처리.
     */
    fun leastStressfulDayOrNull(): StressfulDay? {
        val any = _stress.value ?: return null
        return try {
            // 리플렉션 없이 컴파일 안전을 위해 스마트 캐스트 대신 런타임 접근
            val field = any::class.members.firstOrNull { it.name == "leastStressful" }
            @Suppress("UNCHECKED_CAST")
            (field?.call(any) as? StressfulDay)
        } catch (_: Throwable) {
            null
        }
    }
}
