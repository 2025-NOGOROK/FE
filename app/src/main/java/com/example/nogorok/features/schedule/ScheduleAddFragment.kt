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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.nogorok.network.api.FcmApi
import com.example.nogorok.R
import com.example.nogorok.databinding.FragmentScheduleAddBinding
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.FcmScheduleRequest
import com.example.nogorok.network.dto.GoogleEventAddRequest
import com.example.nogorok.utils.TokenManager
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import android.util.Log

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

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
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
        Log.d("ScheduleAddFragment", "setupAddButton 실행됨")
        binding.btnAddSchedule.setOnClickListener {
            Log.d("ScheduleAddFragment", "Add 버튼 눌림")
            val title = binding.etTitle.text.toString()
            val desc = binding.etDescription.text.toString()

            if (title.isBlank()) {
                Toast.makeText(requireContext(), "제목을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val startDateTime = "${startDate}T${startTime}:00+09:00"
            val endDateTime = "${endDate}T${endTime}:00+09:00"
            val minutesBefore = when (selectedAlarmTime) {
                "5분 전" -> 5
                "10분 전" -> 10
                "30분 전" -> 30
                else -> 10
            }

            val isAlarmChecked = binding.cbMoveAlarm.isChecked


            Log.d("ScheduleAdd", "토큰 가져오기 전")
            val token = TokenManager.getAccessToken(requireContext()) ?: return@setOnClickListener
            Log.d("tokenCheck", "token=$token")
            if (token == null) {
                Toast.makeText(requireContext(), "구글 로그인이 완료되지 않은 상태", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



            val scheduleRequest = GoogleEventAddRequest(
                title = title,
                description = desc,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                serverAlarm = isAlarmChecked,
                minutesBeforeAlarm = minutesBefore
            )

            Log.d("ScheduleAdd", "API 요청 시작")
            Log.d("ScheduleAdd", "startDateTime=$startDateTime, endDateTime=$endDateTime")

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.calendarApi.addGoogleEvent(
                        request = scheduleRequest
                    )
                    if (response.isSuccessful) {
                        // ✅ serverAlarm이 true일 때만 FCM 예약 API 호출
                        if (scheduleRequest.serverAlarm) {
                            val fcmRequest = FcmScheduleRequest(
                                title = "$title 시작 알림",
                                startDateTime = startDateTime,
                                minutesBeforeAlarm = minutesBefore
                            )

                            val fcmResponse = RetrofitClient.fcmApi.registerFcmSchedule(
                                request = fcmRequest
                            )

                            if (fcmResponse.isSuccessful) {
                                Toast.makeText(requireContext(), "일정 및 알림이 등록되었습니다", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), "일정은 추가되었지만 알림 예약 실패", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "일정이 등록되었습니다", Toast.LENGTH_SHORT).show()
                        }

                        findNavController().popBackStack()
                    }
                    else {
                        Toast.makeText(requireContext(), "일정 추가 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "에러 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
