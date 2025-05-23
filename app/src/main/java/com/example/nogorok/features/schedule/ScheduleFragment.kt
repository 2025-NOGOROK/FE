package com.example.nogorok.features.schedule

import android.graphics.Typeface
import android.os.Build
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

    private val viewModel: ScheduleViewModel by activityViewModels()

    // dp 변환 확장 함수
    private val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()

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

        // 상태바 색상 노란색으로 변경
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireActivity().window.statusBarColor =
                ContextCompat.getColor(requireContext(), R.color.bg_calendar_header)
        }

        updateCalendarHeader()

        binding.btnPrevWeek.setOnClickListener {
            baseCalendar.add(Calendar.WEEK_OF_YEAR, -1)
            updateCalendarHeader()
        }
        binding.btnNextWeek.setOnClickListener {
            baseCalendar.add(Calendar.WEEK_OF_YEAR, 1)
            updateCalendarHeader()
        }

        binding.btnCalendar.setOnClickListener {
            // TODO: 캘린더 전체 보기 등 연결
        }

        binding.btnAddSchedule.setOnClickListener {
            viewModel.editingSchedule = null
            findNavController().navigate(R.id.action_scheduleFragment_to_addScheduleFragment)
        }

        viewModel.scheduleList.observe(viewLifecycleOwner) { list ->
            drawScheduleList(list.sortedBy { it.startDate })
        }
    }

    private fun updateCalendarHeader() {
        val year = baseCalendar.get(Calendar.YEAR)
        val month = baseCalendar.get(Calendar.MONTH) + 1
        binding.tvYearMonth.text = "${year}년 ${month}월"

        val weekStart = baseCalendar.clone() as Calendar
        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")
        // 요일+날짜 1:1 세트로 배치
        binding.layoutDaysDates.removeAllViews()

        for (i in 0..6) {
            // 세로 LinearLayout: 요일+날짜 한 세트
            val colLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            // 요일 텍스트
            val dayTv = TextView(requireContext()).apply {
                text = daysOfWeek[i]
                textSize = 14f
                typeface = fontRegular
                setTextColor(0xFF666666.toInt())
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            val dayCal = weekStart.clone() as Calendar
            dayCal.add(Calendar.DAY_OF_MONTH, i)
            if (isSameDay(dayCal, selectedDate)) {
                dayTv.setTypeface(fontBold, Typeface.BOLD)
                dayTv.setTextColor(0xFF73605A.toInt())
            }

            // 날짜 텍스트
            val dateNum = dayCal.get(Calendar.DAY_OF_MONTH)
            val isSelected = isSameDay(dayCal, selectedDate)
            val dateSize = 36.dp // 36dp 고정

            val dateTv = TextView(requireContext()).apply {
                text = dateNum.toString()
                textSize = 14f
                typeface = if (isSelected) fontBold else fontRegular
                setTextColor(if (isSelected) 0xFFF4EED4.toInt() else 0xFF666666.toInt())
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(dateSize, dateSize).apply {
                    topMargin = 4.dp
                    gravity = Gravity.CENTER
                }
                setOnClickListener {
                    selectedDate = dayCal.clone() as Calendar
                    updateCalendarHeader()
                }
                if (isSelected) {
                    background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_selected_date)
                }
            }


            colLayout.addView(dayTv)
            colLayout.addView(dateTv)
            binding.layoutDaysDates.addView(colLayout)
        }
    }

    private fun drawScheduleList(list: List<Schedule>) {
        val layout = binding.layoutScheduleList
        layout.removeAllViews()
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        for (schedule in list) {
            val item = LayoutInflater.from(requireContext()).inflate(R.layout.item_schedule, layout, false)
            item.findViewById<TextView>(R.id.tvTitle).text = schedule.title
            item.findViewById<TextView>(R.id.tvTime).text =
                "${timeFormat.format(schedule.startDate)}~${timeFormat.format(schedule.endDate)}"
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
