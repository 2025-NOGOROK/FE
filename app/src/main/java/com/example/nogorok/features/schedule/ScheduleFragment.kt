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

    // [추가] Date → Calendar 변환 확장 함수
    private fun Date.toCalendar(): Calendar = Calendar.getInstance().apply { time = this@toCalendar }

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

        // [수정 포인트] 선택된 날짜에 해당하는 일정만 보여주기!
        viewModel.scheduleList.observe(viewLifecycleOwner) { list ->
            val filtered = list.filter { schedule ->
                isSameDay(schedule.startDate.toCalendar(), selectedDate)
            }
            drawScheduleList(filtered.sortedBy { it.startDate })
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
                    // [핵심] 날짜를 클릭하면 일정도 바로 새로고침!
                    viewModel.scheduleList.value?.let { list ->
                        val filtered = list.filter { schedule ->
                            isSameDay(schedule.startDate.toCalendar(), selectedDate)
                        }
                        drawScheduleList(filtered.sortedBy { it.startDate })
                    }
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
    // 짧은 쉼표 로딩 프래그먼트와 일정 추가 기능
    fun showShortRestLoadingFragment() {
        // 1. 로딩 프래그먼트 UI를 띄운다 (예시로 DialogFragment 사용, 직접 구현한 프래그먼트로 바꿔도 됨)
        val loadingFragment = com.example.nogorok.features.rest.shortrest.ShortRestLoadingFragment()
        // 선택된 날짜를 넘겨주고 싶으면 아래처럼 setter 사용
        loadingFragment.setSelectedDate(selectedDate.clone() as Calendar)
        loadingFragment.show(parentFragmentManager, "ShortRestLoadingFragment")

        // 2. 3초 후에 로딩 프래그먼트 닫고, 일정 추가
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            // 로딩 프래그먼트 닫기
            loadingFragment.dismissAllowingStateLoss()

            // 랜덤 시간 생성 (예: 20:00~20:30)
            val hour = (18..21).random()
            val minute = listOf(0, 30).random()
            val startCal = (selectedDate.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val endCal = (startCal.clone() as Calendar).apply { add(Calendar.MINUTE, 30) }

            // 일정 데이터 생성
            val newSchedule = Schedule(
                title = "독서하기", // 예시
                description = "짧은 쉼표 추천 일정",
                startDate = startCal.time,
                endDate = endCal.time,
                alarmOption = "알림 없음",
                moveAlarm = false,
                type = "rest"
            )

            // ViewModel에 추가
            viewModel.addSchedule(newSchedule)
        }, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
