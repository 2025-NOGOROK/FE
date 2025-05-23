package com.example.nogorok.features.schedule

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
    private val fontMedium by lazy { ResourcesCompat.getFont(requireContext(), R.font.pretendard_medium) }
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
        binding.layoutDaysDates.removeAllViews()
        val dateSize = 36.dp

        for (i in 0..6) {
            val colLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

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

            val dateNum = dayCal.get(Calendar.DAY_OF_MONTH)
            val isSelected = isSameDay(dayCal, selectedDate)
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
            // 1. 한 줄 전체 컨테이너 (wrap_content로 가운데 정렬)
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 8.dp
                    bottomMargin = 8.dp
                }
            }

            // 2. 아이콘 (핀/쉼표) - 항상 고정 위치, 8dp 마진
            val iconView = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(24.dp, 24.dp).apply {
                    marginEnd = 22.dp // 아이콘과 박스 사이 8dp
                }
                scaleType = ImageView.ScaleType.FIT_CENTER
                when (schedule.type) {
                    "fixed" -> setImageResource(R.drawable.myschedule_pin)
                    "rest" -> setImageResource(R.drawable.ic_myschedule_short_rest_comma)
                    else -> setImageDrawable(null)
                }
            }

            // 3. 일정 박스 (318dp x 54dp, 버튼과 똑같이)
            val scheduleBox = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(318.dp, 54.dp)
                setPadding(20.dp, 0, 20.dp, 0)
                background = when (schedule.type) {
                    "fixed" -> ContextCompat.getDrawable(requireContext(), R.drawable.bg_schedule_fixed)
                    "rest" -> ContextCompat.getDrawable(requireContext(), R.drawable.bg_schedule_rest)
                    else -> null
                }
            }

            // 4. 제목/시간 텍스트
            val tvTitle = TextView(requireContext()).apply {
                text = schedule.title
                textSize = 18f
                typeface = fontRegular
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setTextColor(
                    when (schedule.type) {
                        "fixed" -> Color.parseColor("#73605A")
                        "rest" -> Color.parseColor("#FFF9E2")
                        else -> Color.parseColor("#73605A")
                    }
                )
            }

            val tvTime = TextView(requireContext()).apply {
                text = "${timeFormat.format(schedule.startDate)}~${timeFormat.format(schedule.endDate)}"
                textSize = 16f
                typeface = fontRegular
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setTextColor(
                    when (schedule.type) {
                        "fixed" -> Color.parseColor("#73605A")
                        "rest" -> Color.parseColor("#FFF9E2")
                        else -> Color.parseColor("#73605A")
                    }
                )
            }

            // 5. 박스에 텍스트 추가
            scheduleBox.addView(tvTitle)
            scheduleBox.addView(tvTime)

            // 6. 한 줄에 아이콘 + 박스 추가
            row.addView(iconView)
            row.addView(scheduleBox)

            // 7. 클릭 시 일정 수정 화면 이동
            row.setOnClickListener {
                viewModel.editingSchedule = schedule
                findNavController().navigate(R.id.action_scheduleFragment_to_addScheduleFragment)
            }

            // 8. 리스트에 addView (항상 가운데 정렬)
            layout.addView(row)
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
