package com.example.nogorok.features.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nogorok.databinding.FragmentScheduleBinding
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*
import com.example.nogorok.R

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    val viewModel: ScheduleViewModel by viewModels()

    private var isMonthView = true
    private var currentDate: LocalDate = LocalDate.now()

    private lateinit var scheduleAdapter: ScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupCalendarToggle()
        setupScheduleList()
        setupMonthNavigation()
        setupAddScheduleButton()
        observeViewModel()
        updateCalendar()
    }

    private fun setupCalendarToggle() {
        binding.ivCalendarToggle.setOnClickListener {
            isMonthView = !isMonthView
            updateCalendar()
        }
    }

    private fun setupMonthNavigation() {
        binding.btnPrevMonth.setOnClickListener {
            currentDate = currentDate.minusMonths(1)
            viewModel.selectDate(currentDate)
            updateCalendar()
        }

        binding.btnNextMonth.setOnClickListener {
            currentDate = currentDate.plusMonths(1)
            viewModel.selectDate(currentDate)
            updateCalendar()
        }
    }

    private fun setupAddScheduleButton() {
        binding.btnAddSchedule.setOnClickListener {
            findNavController().navigate(R.id.action_scheduleFragment_to_scheduleAddFragment)
        }
    }

    private fun updateYearMonthLabel() {
        binding.tvYearMonth.text = "${currentDate.year}년 ${currentDate.monthValue}월"
    }

    private fun updateCalendar() {
        updateYearMonthLabel()

        val selected = viewModel.selectedDate.value ?: currentDate
        val dates = if (isMonthView) getMonthDates(currentDate) else getWeekDates(currentDate)

        val adapter = CalendarAdapter(
            dates,
            selected
        ) {
            viewModel.selectDate(it)
            currentDate = it
            updateCalendar()
        }

        binding.rvCalendar.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.rvCalendar.adapter = adapter
    }

    private fun getMonthDates(baseDate: LocalDate): List<LocalDate> {
        val firstDay = baseDate.withDayOfMonth(1)
        val lastDay = baseDate.withDayOfMonth(baseDate.lengthOfMonth())
        val start = firstDay.minusDays(firstDay.dayOfWeek.value % 7L)
        val end = lastDay.plusDays(6 - (lastDay.dayOfWeek.value % 7L))
        return generateDates(start, end)
    }

    private fun getWeekDates(baseDate: LocalDate): List<LocalDate> {
        val weekStart = baseDate.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1)
        return generateDates(weekStart, weekStart.plusDays(6))
    }

    private fun generateDates(start: LocalDate, end: LocalDate): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        var date = start
        while (!date.isAfter(end)) {
            dates.add(date)
            date = date.plusDays(1)
        }
        return dates
    }

    private fun setupScheduleList() {
        scheduleAdapter = ScheduleAdapter()
        binding.rvSchedule.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSchedule.adapter = scheduleAdapter

        viewModel.scheduleList.observe(viewLifecycleOwner) {
            scheduleAdapter.submitList(it)
        }
    }

    private fun observeViewModel() {
        viewModel.selectedDate.observe(viewLifecycleOwner) {
            updateCalendar()
            viewModel.loadSchedules(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
