package com.example.nogorok.features.schedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.nogorok.databinding.FragmentAddScheduleBinding
import androidx.navigation.fragment.findNavController
import com.example.nogorok.R
import java.util.*

class AddScheduleFragment : Fragment() {
    private var _binding: FragmentAddScheduleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ScheduleViewModel by activityViewModels()

    private var startCal: Calendar = Calendar.getInstance()
    private var endCal: Calendar = Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, 1) }
    private var alarmMinute: Int? = null
    private var alarmPopup: PopupWindow? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 수정모드면 데이터 세팅
        viewModel.editingSchedule?.let { schedule ->
            binding.etTitle.setText(schedule.title)
            binding.etDesc.setText(schedule.description)
            binding.btnStartDate.text = schedule.startDate
            binding.btnStartTime.text = schedule.startTime
            binding.btnEndDate.text = schedule.endDate
            binding.btnEndTime.text = schedule.endTime
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnStartDate.setOnClickListener {
            showDatePickerSpinner(startCal) { cal ->
                startCal.time = cal.time
                binding.btnStartDate.text = formatDate(cal)
            }
        }
        binding.btnStartTime.setOnClickListener {
            showTimePickerSpinner(startCal) { cal ->
                startCal.time = cal.time
                binding.btnStartTime.text = formatTime(cal)
            }
        }
        binding.btnEndDate.setOnClickListener {
            showDatePickerSpinner(endCal) { cal ->
                endCal.time = cal.time
                binding.btnEndDate.text = formatDate(cal)
            }
        }
        binding.btnEndTime.setOnClickListener {
            showTimePickerSpinner(endCal) { cal ->
                endCal.time = cal.time
                binding.btnEndTime.text = formatTime(cal)
            }
        }

        binding.btnAlarmTime.setOnClickListener {
            showAlarmPopup()
        }

        binding.btnAdd.setOnClickListener {
            val schedule = Schedule(
                id = viewModel.editingSchedule?.id ?: System.currentTimeMillis(),
                title = binding.etTitle.text.toString(),
                description = binding.etDesc.text.toString(),
                startDate = binding.btnStartDate.text.toString(),
                startTime = binding.btnStartTime.text.toString(),
                endDate = binding.btnEndDate.text.toString(),
                endTime = binding.btnEndTime.text.toString()
            )
            if (viewModel.editingSchedule == null) {
                viewModel.addSchedule(schedule)
            } else {
                viewModel.updateSchedule(schedule)
            }
            viewModel.editingSchedule = null
            findNavController().popBackStack()
        }
    }

    private fun formatDate(cal: Calendar): String {
        return "${cal.get(Calendar.YEAR)}.${cal.get(Calendar.MONTH)+1}.${cal.get(Calendar.DAY_OF_MONTH)}."
    }

    private fun formatTime(cal: Calendar): String {
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val ampm = if (hour < 12) "오전" else "오후"
        val h = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        return "$ampm $h:%02d".format(minute)
    }

    private fun showDatePickerSpinner(cal: Calendar, onDateSet: (Calendar) -> Unit) {
        val dialog = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DatePickerDialog(
                requireContext(),
                R.style.DatePickerSpinner,
                { _, y, m, d ->
                    cal.set(Calendar.YEAR, y)
                    cal.set(Calendar.MONTH, m)
                    cal.set(Calendar.DAY_OF_MONTH, d)
                    onDateSet(cal)
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            )
        } else {
            DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    cal.set(Calendar.YEAR, y)
                    cal.set(Calendar.MONTH, m)
                    cal.set(Calendar.DAY_OF_MONTH, d)
                    onDateSet(cal)
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            )
        }
        dialog.show()
        dialog.getButton(DatePickerDialog.BUTTON_POSITIVE)?.setTextColor(0xFF000000.toInt())
        dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setTextColor(0xFF000000.toInt())
    }

    private fun showTimePickerSpinner(cal: Calendar, onTimeSet: (Calendar) -> Unit) {
        val dialog = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TimePickerDialog(
                requireContext(),
                R.style.TimePickerSpinner,
                { _, h, min ->
                    cal.set(Calendar.HOUR_OF_DAY, h)
                    cal.set(Calendar.MINUTE, min)
                    onTimeSet(cal)
                },
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false
            )
        } else {
            TimePickerDialog(
                requireContext(),
                { _, h, min ->
                    cal.set(Calendar.HOUR_OF_DAY, h)
                    cal.set(Calendar.MINUTE, min)
                    onTimeSet(cal)
                },
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false
            )
        }
        dialog.show()
        dialog.getButton(TimePickerDialog.BUTTON_POSITIVE)?.setTextColor(0xFF000000.toInt())
        dialog.getButton(TimePickerDialog.BUTTON_NEGATIVE)?.setTextColor(0xFF000000.toInt())
    }

    private fun showAlarmPopup() {
        alarmPopup?.dismiss()

        val popupView = LayoutInflater.from(requireContext())
            .inflate(R.layout.layout_alarm_picker_popup, null, false)

        val anchorWidth = binding.btnAlarmTime.width
        popupView.layoutParams = ViewGroup.LayoutParams(anchorWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

        val popupWindow = PopupWindow(
            popupView,
            anchorWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnMinus = popupView.findViewById<Button>(R.id.btn_alarm_minus)
        val btnPlus = popupView.findViewById<Button>(R.id.btn_alarm_plus)
        val etMinute = popupView.findViewById<EditText>(R.id.et_alarm_minute)

        btnMinus.setTextColor(Color.BLACK)
        btnPlus.setTextColor(Color.BLACK)

        val initialValue = alarmMinute ?: 10
        etMinute.setText(initialValue.toString())

        btnMinus.setOnClickListener {
            val value = (etMinute.text.toString().toIntOrNull() ?: 10) - 1
            if (value >= 1) {
                etMinute.setText(value.toString())
            }
        }
        btnPlus.setOnClickListener {
            val value = (etMinute.text.toString().toIntOrNull() ?: 10) + 1
            etMinute.setText(value.toString())
        }
        etMinute.setOnEditorActionListener { _, actionId, _ ->
            popupWindow.dismiss()
            true
        }

        popupWindow.setOnDismissListener {
            val value = etMinute.text.toString().toIntOrNull()
            if (value != null && value > 0) {
                alarmMinute = value
                binding.btnAlarmTime.text = "${value}분 전"
            } else {
                alarmMinute = null
                binding.btnAlarmTime.text = "알림 없음"
            }
            alarmPopup = null
        }

        binding.btnAlarmTime.post {
            val location = IntArray(2)
            binding.btnAlarmTime.getLocationOnScreen(location)
            popupView.measure(
                View.MeasureSpec.makeMeasureSpec(anchorWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.UNSPECIFIED
            )
            val popupHeight = popupView.measuredHeight
            popupWindow.showAtLocation(
                binding.btnAlarmTime,
                Gravity.NO_GRAVITY,
                location[0],
                location[1] - popupHeight
            )
        }
        alarmPopup = popupWindow
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        alarmPopup = null
    }
}
