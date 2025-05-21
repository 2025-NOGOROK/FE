package com.example.nogorok.features.schedule

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nogorok.R
import com.example.nogorok.databinding.FragmentAddScheduleBinding
import java.text.SimpleDateFormat
import java.util.*

class AddScheduleFragment : Fragment() {

    private var _binding: FragmentAddScheduleBinding? = null
    private val binding get() = _binding!!

    private val scheduleViewModel: ScheduleViewModel by activityViewModels()

    private var startCalendar: Calendar = Calendar.getInstance()
    private var endCalendar: Calendar = Calendar.getInstance()
    private var alarmMinute: Int = 0
    private var moveAlarm: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 수정모드라면 기존 데이터로 입력란 채우기
        val editing = scheduleViewModel.editingSchedule
        if (editing != null) {
            binding.etTitle.setText(editing.title)
            binding.etDesc.setText(editing.description)
            startCalendar.time = editing.startDate
            endCalendar.time = editing.endDate
            alarmMinute = editing.alarmOption.filter { it.isDigit() }.toIntOrNull() ?: 0
            moveAlarm = editing.moveAlarm
            binding.cbAlarm.isChecked = moveAlarm
            updateDateTimeViews()
            updateAlarmMinuteView()
        }

        // 뒤로가기
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // 제목/설명 입력 시 색상 변경 및 추가 버튼 활성화
        binding.etTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.etTitle.setTextColor(
                    if (s.isNullOrEmpty()) 0xFF7F7C7C.toInt() else 0xFF000000.toInt()
                )
                checkButtonEnable()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.etDesc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.etDesc.setTextColor(
                    if (s.isNullOrEmpty()) 0xFF7F7C7C.toInt() else 0xFF000000.toInt()
                )
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 날짜/시간 피커
        binding.tvStartDate.setOnClickListener { showDatePicker(true) }
        binding.tvStartTime.setOnClickListener { showTimePicker(true) }
        binding.tvEndDate.setOnClickListener { showDatePicker(false) }
        binding.tvEndTime.setOnClickListener { showTimePicker(false) }

        // 푸쉬 알림 설정: 알림 없음 박스 클릭 시 다이얼로그로 5분 단위 증감
        binding.layoutAlarmControl.setOnClickListener {
            showAlarmMinutePicker()
        }

        // 이동 전 알림받기 체크박스: 체크/해제 모두 가능, 값 동기화
        binding.cbAlarm.setOnCheckedChangeListener { _, isChecked ->
            moveAlarm = isChecked
        }

        // 추가/수정 버튼
        binding.btnAdd.setOnClickListener {
            val newSchedule = Schedule(
                title = binding.etTitle.text.toString(),
                description = binding.etDesc.text.toString(),
                startDate = startCalendar.time,
                endDate = endCalendar.time,
                alarmOption = if (alarmMinute == 0) "알림 없음" else "${alarmMinute}분 전",
                moveAlarm = moveAlarm
            )
            if (editing != null) {
                // 수정모드: 기존 일정 덮어쓰기
                scheduleViewModel.updateSchedule(editing, newSchedule)
            } else {
                // 추가모드: 새 일정 추가
                scheduleViewModel.addSchedule(newSchedule)
            }
            findNavController().popBackStack()
        }

        // 초기값 세팅
        updateDateTimeViews()
        updateAlarmMinuteView()
        checkButtonEnable()
        binding.cbAlarm.isChecked = moveAlarm
    }

    private fun checkButtonEnable() {
        binding.btnAdd.isEnabled = binding.etTitle.text.isNotEmpty()
    }

    // 날짜 선택: yyyy.mm.dd NumberPicker + 하단 버튼
    private fun showDatePicker(isStart: Boolean) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_date_picker)

        val yearPicker = dialog.findViewById<NumberPicker>(R.id.yearPicker)
        val monthPicker = dialog.findViewById<NumberPicker>(R.id.monthPicker)
        val dayPicker = dialog.findViewById<NumberPicker>(R.id.dayPicker)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        val calendar = if (isStart) startCalendar else endCalendar

        yearPicker.minValue = 2020
        yearPicker.maxValue = 2030
        yearPicker.value = calendar.get(Calendar.YEAR)
        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = calendar.get(Calendar.MONTH) + 1
        dayPicker.minValue = 1
        dayPicker.maxValue = 31
        dayPicker.value = calendar.get(Calendar.DAY_OF_MONTH)

        btnOk.setOnClickListener {
            calendar.set(Calendar.YEAR, yearPicker.value)
            calendar.set(Calendar.MONTH, monthPicker.value - 1)
            calendar.set(Calendar.DAY_OF_MONTH, dayPicker.value)
            updateDateTimeViews()
            dialog.dismiss()
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    // 시간 선택: 오전/오후, 시, 분 NumberPicker + 하단 버튼
    private fun showTimePicker(isStart: Boolean) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_time_picker)

        val amPmPicker = dialog.findViewById<NumberPicker>(R.id.amPmPicker)
        val hourPicker = dialog.findViewById<NumberPicker>(R.id.hourPicker)
        val minutePicker = dialog.findViewById<NumberPicker>(R.id.minutePicker)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        val calendar = if (isStart) startCalendar else endCalendar

        amPmPicker.minValue = 0
        amPmPicker.maxValue = 1
        amPmPicker.displayedValues = arrayOf("오전", "오후")
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        amPmPicker.value = if (hourOfDay < 12) 0 else 1

        hourPicker.minValue = 1
        hourPicker.maxValue = 12
        hourPicker.value = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12

        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        minutePicker.value = calendar.get(Calendar.MINUTE)

        btnOk.setOnClickListener {
            var hour = hourPicker.value
            if (amPmPicker.value == 1 && hour != 12) hour += 12
            if (amPmPicker.value == 0 && hour == 12) hour = 0
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minutePicker.value)
            updateDateTimeViews()
            dialog.dismiss()
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    // 푸쉬 알림 설정: 5분 단위 증감 다이얼로그
    private fun showAlarmMinutePicker() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_alarm_picker)

        val btnMinus = dialog.findViewById<Button>(R.id.btnMinus)
        val btnPlus = dialog.findViewById<Button>(R.id.btnPlus)
        val tvMinute = dialog.findViewById<TextView>(R.id.tvMinute)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        var minute = alarmMinute
        fun updateView() {
            tvMinute.text = if (minute == 0) "알림 없음" else "$minute 분 전"
        }
        updateView()

        btnMinus.setOnClickListener {
            if (minute > 0) {
                minute -= 5
                updateView()
            }
        }
        btnPlus.setOnClickListener {
            minute += 5
            updateView()
        }
        btnOk.setOnClickListener {
            alarmMinute = minute
            updateAlarmMinuteView()
            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun updateAlarmMinuteView() {
        binding.tvAlarmMinute.text = if (alarmMinute == 0) "알림 없음" else "${alarmMinute}분 전"
    }

    private fun updateDateTimeViews() {
        val dateFormat = SimpleDateFormat("yyyy.M.d.", Locale.getDefault())
        val timeFormat = SimpleDateFormat("a h:mm", Locale.getDefault())
        binding.tvStartDate.text = dateFormat.format(startCalendar.time)
        binding.tvStartTime.text = timeFormat.format(startCalendar.time)
        binding.tvEndDate.text = dateFormat.format(endCalendar.time)
        binding.tvEndTime.text = timeFormat.format(endCalendar.time)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
