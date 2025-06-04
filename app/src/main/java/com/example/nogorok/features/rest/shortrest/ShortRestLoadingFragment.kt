package com.example.nogorok.features.rest.shortrest

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.nogorok.R
import com.example.nogorok.features.schedule.Schedule
import com.example.nogorok.features.schedule.ScheduleViewModel
import java.util.*
import kotlin.random.Random
import androidx.fragment.app.DialogFragment

/**
 * 짧은 쉼표 로딩 프래그먼트
 * 3초간 로딩 후, 일정 자동 추가 및 화면에서 사라짐
 */
class ShortRestLoadingFragment : DialogFragment() {

    // ViewModel은 액티비티 범위로 공유
    private val scheduleViewModel: ScheduleViewModel by activityViewModels()

    // 선택된 날짜를 받아오기 위한 콜백 (부모 Fragment에서 전달)
    private var selectedDate: Calendar? = null

    // 외부에서 날짜를 세팅할 수 있게 setter 제공
    fun setSelectedDate(calendar: Calendar) {
        selectedDate = calendar
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 레이아웃 inflate
        return inflater.inflate(R.layout.fragment_short_rest_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar)
        // 3초 후에 일정 추가 및 프래그먼트 종료
        Handler(Looper.getMainLooper()).postDelayed({
            // Fragment가 아직 화면에 살아있을 때만 안전하게 닫기!
            if (isAdded && !isRemoving && !isDetached && dialog?.isShowing == true) {
                // 1. 랜덤 짧은 쉼표 일정 생성
                addRandomShortRestSchedule()
                // 2. 프래그먼트 안전하게 닫기
                dismissAllowingStateLoss()
            }
        }, 3000)
    }

    /**
     * 선택된 날짜에 랜덤 짧은 쉼표 일정 추가
     */
    private fun addRandomShortRestSchedule() {
        val calendar = selectedDate ?: Calendar.getInstance()
        // 랜덤 시간대 생성 (예: 20:00~20:10)
        val hour = Random.nextInt(8, 22) // 8시~21시
        val minute = listOf(0, 30).random()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startDate = calendar.time

        // 30분 뒤로 종료시간 설정
        val endCalendar = calendar.clone() as Calendar
        endCalendar.add(Calendar.MINUTE, 30)
        val endDate = endCalendar.time

        // 일정 객체 생성 (type = "rest"가 핵심!)
        val schedule = Schedule(
            title = "독서하기", // 예시, 랜덤 문구로 바꿔도 됨
            description = "짧은 쉼표 추천 일정",
            startDate = startDate,
            endDate = endDate,
            alarmOption = "알림 없음",
            moveAlarm = false,
            type = "rest"
        )

        // ViewModel에 추가
        scheduleViewModel.addSchedule(schedule)
    }
}
