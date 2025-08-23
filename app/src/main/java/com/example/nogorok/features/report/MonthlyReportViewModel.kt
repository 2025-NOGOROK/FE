package com.example.nogorok.features.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate

class MonthlyReportViewModel : ViewModel() {

    private val _counts = MutableLiveData<MonthlyCountsDto?>()
    val counts: LiveData<MonthlyCountsDto?> = _counts

    private val _stress = MutableLiveData<MonthlyStressDto?>()
    val stress: LiveData<MonthlyStressDto?> = _stress

    // ✅ 추가: 트렌드 데이터
    private val _trend = MutableLiveData<MonthlyStressTrendDto?>()
    val trend: LiveData<MonthlyStressTrendDto?> = _trend

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /** 현재 연/월 기준으로 3개 API 동시 조회 */
    fun load(year: Int = LocalDate.now().year, month: Int = LocalDate.now().monthValue) {
        viewModelScope.launch {
            try {
                val countsDefer = async { RetrofitClient.monthlyApi.getMonthly(year, month) }
                val stressDefer = async { RetrofitClient.monthlyApi.getMonthlyStress(year, month) }
                val trendDefer  = async { RetrofitClient.monthlyApi.getMonthlyStressTrend() }

                countsDefer.await().let { res ->
                    if (res.isSuccessful) _counts.postValue(res.body())
                    else {
                        _counts.postValue(null)
                        _error.postValue("monthly 실패: ${res.code()}")
                    }
                }

                stressDefer.await().let { res ->
                    if (res.isSuccessful) _stress.postValue(res.body())
                    else {
                        _stress.postValue(null)
                        _error.postValue("monthly-stress 실패: ${res.code()}")
                    }
                }

                trendDefer.await().let { res ->
                    if (res.isSuccessful) _trend.postValue(res.body())
                    else {
                        _trend.postValue(null)
                        _error.postValue("monthly-stress-trend 실패: ${res.code()}")
                    }
                }
            } catch (t: Throwable) {
                _error.postValue("네트워크 오류: ${t.message}")
                _counts.postValue(null)
                _stress.postValue(null)
                _trend.postValue(null)
            }
        }
    }

    /** 액티비티가 차트에 바로 넣기 쉬운 형태로 변환 */
    fun trendAsChartData(): Pair<List<Float>, List<String>> {
        val dto = _trend.value ?: return emptyList<Float>() to emptyList()
        // 좌→우가 시간 증가하도록 정렬(연/월 오름차순)
        val sorted = dto.points.sortedWith(compareBy({ it.year }, { it.month }))
        val values = sorted.map { it.value }
        val labels = sorted.map { "${it.month}월" }
        return values to labels
    }

    fun mostStressfulDay(): StressfulDay? = _stress.value?.mostStressful
    fun leastStressfulDay(): StressfulDay? = _stress.value?.leastStressful
}
