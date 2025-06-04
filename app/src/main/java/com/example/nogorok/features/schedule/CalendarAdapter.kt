package com.example.nogorok.features.schedule

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nogorok.R
import com.example.nogorok.databinding.ItemCalendarDayBinding
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

class CalendarAdapter(
    private val dates: List<LocalDate>,
    private val selectedDate: LocalDate,
    private val onDateClick: (LocalDate) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER = 0
    private val TYPE_DATE = 1

    inner class HeaderViewHolder(private val textView: TextView) : RecyclerView.ViewHolder(textView) {
        fun bind(dayOfWeek: String) {
            textView.text = dayOfWeek
        }
    }

    inner class DateViewHolder(private val binding: ItemCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(date: LocalDate) {
            val isSameMonth = date.month == selectedDate.month && date.year == selectedDate.year

            if (isSameMonth) {
                binding.tvDay.text = date.dayOfMonth.toString()
                binding.tvDay.visibility = View.VISIBLE
            } else {
                binding.tvDay.text = ""
                binding.tvDay.visibility = View.INVISIBLE
            }

            if (date == selectedDate && isSameMonth) {
                binding.tvDay.setBackgroundResource(R.drawable.bg_day_selector)
                binding.tvDay.setTextColor(Color.WHITE)
            } else {
                binding.tvDay.setBackgroundResource(android.R.color.transparent)
                binding.tvDay.setTextColor(Color.BLACK)
            }

            if (isSameMonth) {
                binding.tvDay.setOnClickListener { onDateClick(date) }
            } else {
                binding.tvDay.setOnClickListener(null)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < 7) TYPE_HEADER else TYPE_DATE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val textView = TextView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    parent.measuredWidth / 7,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                setTextColor(Color.DKGRAY)
                textSize = 14f
            }
            HeaderViewHolder(textView)
        } else {
            val binding = ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val layoutParams = binding.root.layoutParams
            layoutParams.width = parent.measuredWidth / 7
            binding.root.layoutParams = layoutParams
            DateViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            val days = listOf("일", "월", "화", "수", "목", "금", "토")
            holder.bind(days[position])
        } else if (holder is DateViewHolder) {
            holder.bind(dates[position - 7])
        }
    }

    override fun getItemCount(): Int = dates.size + 7
}
