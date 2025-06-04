package com.example.nogorok.features.schedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.nogorok.databinding.FragmentScheduleAddBinding
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class ScheduleAddFragment : Fragment() {

    private var _binding: FragmentScheduleAddBinding? = null
    private val binding get() = _binding!!

    private var startTime: LocalTime = LocalTime.of(9, 0)
    private var endTime: LocalTime = LocalTime.of(10, 0)
    private var startDate: LocalDate = LocalDate.now()
    private var endDate: LocalDate = LocalDate.now()

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy.M.d")
    private val timeFormatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREA)

    private var selectedAlarmTime: String = "10분 전"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupDefaultDateTime()
        setupDatePickers()
        setupTimePickers()
        setupAlarmSpinner()
        setupAddButton()
    }

    private fun setupDefaultDateTime() {
        binding.tvStartDate.text = startDate.format(dateFormatter)
        binding.tvEndDate.text = endDate.format(dateFormatter)
        binding.tvStartTime.text = startTime.format(timeFormatter)
        binding.tvEndTime.text = endTime.format(timeFormatter)
    }

    private fun setupDatePickers() {
        binding.tvStartDate.setOnClickListener {
            showDatePicker(startDate) { selected ->
                startDate = selected
                binding.tvStartDate.text = selected.format(dateFormatter)
            }
        }

        binding.tvEndDate.setOnClickListener {
            showDatePicker(endDate) { selected ->
                endDate = selected
                binding.tvEndDate.text = selected.format(dateFormatter)
            }
        }
    }

    private fun showDatePicker(initialDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
        val dialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val picked = LocalDate.of(year, month + 1, dayOfMonth)
                onDateSelected(picked)
            },
            initialDate.year,
            initialDate.monthValue - 1,
            initialDate.dayOfMonth
        )

        dialog.setOnDismissListener {
            val picker = dialog.datePicker
            val picked = LocalDate.of(picker.year, picker.month + 1, picker.dayOfMonth)
            onDateSelected(picked)
        }

        dialog.show()
    }

    private fun setupTimePickers() {
        binding.tvStartTime.setOnClickListener {
            showTimePicker(startTime) { selected ->
                startTime = selected
                binding.tvStartTime.text = selected.format(timeFormatter)
            }
        }

        binding.tvEndTime.setOnClickListener {
            showTimePicker(endTime) { selected ->
                endTime = selected
                binding.tvEndTime.text = selected.format(timeFormatter)
            }
        }
    }

    private fun showTimePicker(initial: LocalTime, onTimeSelected: (LocalTime) -> Unit) {
        var pickedTime: LocalTime? = null

        val dialog = TimePickerDialog(
            requireContext(),
            { _: TimePicker, hour: Int, minute: Int ->
                pickedTime = LocalTime.of(hour, minute)
            },
            initial.hour,
            initial.minute,
            false
        )

        dialog.setOnDismissListener {
            pickedTime?.let { onTimeSelected(it) }
        }

        dialog.show()
    }

    private fun setupAlarmSpinner() {
        val options = listOf("5분 전", "10분 전", "30분 전")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAlarmTime.adapter = adapter

        val defaultIndex = options.indexOf("10분 전")
        binding.spinnerAlarmTime.setSelection(defaultIndex)

        binding.spinnerAlarmTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                selectedAlarmTime = options[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupAddButton() {
        binding.btnAddSchedule.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val desc = binding.etDescription.text.toString()

            if (title.isBlank()) {
                Toast.makeText(requireContext(), "제목을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(
                requireContext(),
                "일정이 추가되었습니다\n알림: $selectedAlarmTime",
                Toast.LENGTH_SHORT
            ).show()

            // TODO: 데이터 저장 처리
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
