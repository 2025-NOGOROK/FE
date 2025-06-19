package com.example.nogorok.features.schedule

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nogorok.R
import com.example.nogorok.databinding.ItemScheduleBinding

class ScheduleAdapter :
    ListAdapter<ScheduleItem, ScheduleAdapter.ScheduleViewHolder>(diffUtil) {

    inner class ScheduleViewHolder(private val binding: ItemScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ScheduleItem) {
            binding.tvTitle.text = item.title
            binding.tvTime.text = "${item.startTime} - ${item.endTime}"

            val isRecommend = item.sourceType == "short-recommend" || item.sourceType == "long-recommend"

            binding.cardSchedule.setBackgroundResource(
                if (isRecommend) R.drawable.bg_schedule_short_rest
                else R.drawable.bg_schedule_normal
            )

            val textColor = if (isRecommend) Color.parseColor("#FFFBEA") else Color.parseColor("#6A4A38")
            binding.tvTitle.setTextColor(textColor)
            binding.tvTime.setTextColor(textColor)

            binding.ivSymbol.apply {
                visibility = View.VISIBLE
                setImageResource(if (isRecommend) R.drawable.comma else R.drawable.pin)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ScheduleItem>() {
            override fun areItemsTheSame(oldItem: ScheduleItem, newItem: ScheduleItem): Boolean {
                return oldItem.title == newItem.title &&
                        oldItem.startTime == newItem.startTime &&
                        oldItem.endTime == newItem.endTime &&
                        oldItem.sourceType == newItem.sourceType
            }

            override fun areContentsTheSame(oldItem: ScheduleItem, newItem: ScheduleItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
