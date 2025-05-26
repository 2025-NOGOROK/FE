package com.example.nogorok.features.schedule

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
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
    private var alarmMinute: Int = 0 // ⭐️ 기본값 "알림 없음"으로!
    private var moveAlarm: Boolean = false

    private var endTimeManuallyChanged: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        } else {
            endCalendar.timeInMillis = startCalendar.timeInMillis
            endCalendar.add(Calendar.HOUR_OF_DAY, 1)
            updateDateTimeViews()
            updateAlarmMinuteView()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

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

        // 날짜/시간 피커 (휠)
        binding.tvStartDate.setOnClickListener { showDatePicker(true) }
        binding.tvStartTime.setOnClickListener { showTimePicker(true) }
        binding.tvEndDate.setOnClickListener {
            endTimeManuallyChanged = true
            showDatePicker(false)
        }
        binding.tvEndTime.setOnClickListener {
            endTimeManuallyChanged = true
            showTimePicker(false)
        }

        // 🛎️ 푸쉬 알림 시간 박스 클릭 시 리스트 다이얼로그 띄우기
        binding.tvPushAlarmMinute.setOnClickListener {
            showPushAlarmOptionDialog()
        }

        // 이동 전 알림 체크박스
        binding.cbAlarm.setOnCheckedChangeListener { _, isChecked ->
            moveAlarm = isChecked
        }

        // 일정 추가 버튼
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
                scheduleViewModel.updateSchedule(editing, newSchedule)
            } else {
                scheduleViewModel.addSchedule(newSchedule)
            }
            findNavController().popBackStack()
        }

        updateDateTimeViews()
        updateAlarmMinuteView()
        checkButtonEnable()
        binding.cbAlarm.isChecked = moveAlarm
    }

    private fun checkButtonEnable() {
        binding.btnAdd.isEnabled = binding.etTitle.text.isNotEmpty()
    }

    // 날짜 선택: 커스텀 다이얼로그 + NumberPicker (휠)
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

            if (isStart && !endTimeManuallyChanged) {
                endCalendar.set(Calendar.YEAR, yearPicker.value)
                endCalendar.set(Calendar.MONTH, monthPicker.value - 1)
                endCalendar.set(Calendar.DAY_OF_MONTH, dayPicker.value)
                endCalendar.timeInMillis = startCalendar.timeInMillis
                endCalendar.add(Calendar.HOUR_OF_DAY, 1)
                updateDateTimeViews()
            }
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    // 시간 선택: 커스텀 다이얼로그 + NumberPicker (휠)
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

            if (isStart && !endTimeManuallyChanged) {
                endCalendar.timeInMillis = startCalendar.timeInMillis
                endCalendar.add(Calendar.HOUR_OF_DAY, 1)
                updateDateTimeViews()
            }
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    // 🛎️ 푸쉬 알림 옵션 리스트 다이얼로그 함수 (AlertDialog)
    private fun showPushAlarmOptionDialog() {
        val options = arrayOf("알림 없음", "10분 전", "30분 전", "1시간 전")
        val checkedItem = when (alarmMinute) {
            0 -> 0
            10 -> 1
            30 -> 2
            60 -> 3
            else -> 0
        }

        var selectedMinute = alarmMinute // 임시 선택값

        val builder = AlertDialog.Builder(requireContext())
            .setTitle("푸쉬 알림 시간 선택")
            .setSingleChoiceItems(options, checkedItem) { _, which ->
                // 선택 시 임시 저장
                selectedMinute = when (which) {
                    0 -> 0
                    1 -> 10
                    2 -> 30
                    3 -> 60
                    else -> 0
                }
            }
            .setNegativeButton("취소", null)
            .setPositiveButton("확인") { _, _ ->
                alarmMinute = selectedMinute
                updateAlarmMinuteView()
            }

        val dialog = builder.create()
        dialog.setOnShowListener {
            // 버튼 색상 검정(#000000)으로 변경!
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.parseColor("#000000"))
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.parseColor("#000000"))
        }
        dialog.show()
    }

    // "알림 없음"일 때 12sp, 숫자일 때 14sp로 동적 변경!
    private fun updateAlarmMinuteView() {
        if (alarmMinute == 0) {
            binding.tvPushAlarmMinute.text = "알림 없음"
            binding.tvPushAlarmMinute.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        } else {
            binding.tvPushAlarmMinute.text = when (alarmMinute) {
                10 -> "10분 전"
                30 -> "30분 전"
                60 -> "1시간 전"
                else -> "${alarmMinute}분 전"
            }
            binding.tvPushAlarmMinute.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        }
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
