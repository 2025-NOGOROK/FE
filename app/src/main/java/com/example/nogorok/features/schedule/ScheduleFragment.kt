package com.example.nogorok.features.schedule

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nogorok.R
import com.example.nogorok.databinding.FragmentScheduleBinding
import java.text.SimpleDateFormat
import java.util.*

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private val fontRegular by lazy { ResourcesCompat.getFont(requireContext(), R.font.pretendard_regular) }
    private val fontBold by lazy { ResourcesCompat.getFont(requireContext(), R.font.pretendard_bold) }

    private var baseCalendar = Calendar.getInstance()
    private var selectedDate: Calendar = Calendar.getInstance()

    // ViewModel은 네가 만든 ScheduleViewModel을 그대로 사용
    private val viewModel: ScheduleViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateCalendarHeader()

        binding.btnPrevWeek.setOnClickListener {
            baseCalendar.add(Calendar.WEEK_OF_YEAR, -1)
            updateCalendarHeader()
        }
        binding.btnNextWeek.setOnClickListener {
            baseCalendar.add(Calendar.WEEK_OF_YEAR, 1)
            updateCalendarHeader()
        }

        // 일정 추가하기 버튼 클릭 시 수정모드 해제
        binding.btnAddSchedule.setOnClickListener {
            viewModel.editingSchedule = null // 새 일정 추가
            findNavController().navigate(R.id.action_scheduleFragment_to_addScheduleFragment)
        }

        // 일정 리스트 관찰해서 그리기
        viewModel.scheduleList.observe(viewLifecycleOwner) { list ->
            drawScheduleList(list)
        }
    }

    private fun updateCalendarHeader() {
        val year = baseCalendar.get(Calendar.YEAR)
        val month = baseCalendar.get(Calendar.MONTH) + 1
        binding.tvYearMonth.text = "${year}년 ${month}월"

        val weekStart = baseCalendar.clone() as Calendar
        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")
        binding.layoutDays.removeAllViews()
        for (i in 0..6) {
            val tv = TextView(requireContext()).apply {
                text = daysOfWeek[i]
                textSize = 14f
                typeface = fontRegular
                setTextColor(0xFF666666.toInt())
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            val dayCal = weekStart.clone() as Calendar
            dayCal.add(Calendar.DAY_OF_MONTH, i)
            if (isSameDay(dayCal, selectedDate)) {
                tv.setTypeface(fontBold, Typeface.BOLD)
                tv.setTextColor(0xFF73605A.toInt())
            }
            binding.layoutDays.addView(tv)
        }

        binding.layoutDates.removeAllViews()
        val dateSize = resources.getDimensionPixelSize(R.dimen.calendar_date_size)
        for (i in 0..6) {
            val dayCal = weekStart.clone() as Calendar
            dayCal.add(Calendar.DAY_OF_MONTH, i)
            val dateNum = dayCal.get(Calendar.DAY_OF_MONTH)
            val isSelected = isSameDay(dayCal, selectedDate)
            val tv = TextView(requireContext()).apply {
                text = dateNum.toString()
                textSize = 14f
                typeface = if (isSelected) fontBold else fontRegular
                setTextColor(if (isSelected) 0xFFF4EED4.toInt() else 0xFF666666.toInt())
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(dateSize, dateSize).apply {
                    weight = 1f
                }
                setOnClickListener {
                    selectedDate = dayCal.clone() as Calendar
                    updateCalendarHeader()
                }
                if (isSelected) {
                    background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_selected_date)
                }
            }
            binding.layoutDates.addView(tv)
        }
    }

    private fun drawScheduleList(list: List<Schedule>) {
        val layout = binding.layoutScheduleList
        layout.removeAllViews()
        // 시간 포맷터 준비 (오전 9:00)
        val timeFormat = SimpleDateFormat("a h:mm", Locale.getDefault())
        // 날짜 포맷터 (2025.3.15.)
        val dateFormat = SimpleDateFormat("yyyy.M.d.", Locale.getDefault())
        for (schedule in list) {
            val item = LayoutInflater.from(requireContext()).inflate(R.layout.item_schedule, layout, false)
            item.findViewById<TextView>(R.id.tvTitle).text = schedule.title
            // startDate, endDate를 날짜+시간 형태로 포맷팅
            val startDateStr = dateFormat.format(schedule.startDate)
            val startTimeStr = timeFormat.format(schedule.startDate)
            val endDateStr = dateFormat.format(schedule.endDate)
            val endTimeStr = timeFormat.format(schedule.endDate)
            // 원하는 형식에 맞게 표시 (여기선 날짜와 시간 모두 표시)
            item.findViewById<TextView>(R.id.tvTime).text = "$startDateStr $startTimeStr ~ $endDateStr $endTimeStr"
            // 클릭 시 수정모드로 이동
            item.setOnClickListener {
                viewModel.editingSchedule = schedule
                findNavController().navigate(R.id.action_scheduleFragment_to_addScheduleFragment)
            }
            layout.addView(item)
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
