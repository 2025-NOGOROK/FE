package com.example.nogorok.features.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nogorok.R
import com.example.nogorok.databinding.ItemCalendarDayBinding
import java.time.LocalDate

class CalendarAdapter(
    private val dates: List<LocalDate>,
    private val selectedDate: LocalDate,
    private val onDateClick: (LocalDate) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.DateViewHolder>() {

    inner class DateViewHolder(val binding: ItemCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(date: LocalDate) {
            binding.tvDay.text = date.dayOfMonth.toString()

            if (date == selectedDate) {
                binding.tvDay.setBackgroundResource(R.drawable.bg_day_selector)
            } else {
                binding.tvDay.setBackgroundResource(android.R.color.transparent)
            }

            binding.tvDay.setOnClickListener {
                onDateClick(date)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding = ItemCalendarDayBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(dates[position])
    }

    override fun getItemCount(): Int = dates.size
}
