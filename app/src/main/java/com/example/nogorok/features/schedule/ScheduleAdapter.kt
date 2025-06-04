package com.example.nogorok.features.schedule

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nogorok.databinding.ItemScheduleBinding

class ScheduleAdapter :
    ListAdapter<ScheduleItem, ScheduleAdapter.ScheduleViewHolder>(diffUtil) {

    inner class ScheduleViewHolder(private val binding: ItemScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ScheduleItem) {
            binding.tvTitle.text = item.title
            binding.tvTime.text = item.time

            // 📌 핀 표시 여부
            binding.layoutPinned.visibility = if (item.isPinned) View.VISIBLE else View.GONE

            // 🟤 배경색 분기 처리
            binding.root.setCardBackgroundColor(
                if (item.isShortRest) Color.parseColor("#6A4E42") // 짧은 쉼표: 브라운
                else Color.parseColor("#FFFBEA")                  // 일반 일정: 연노랑
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemScheduleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ScheduleItem>() {
            override fun areItemsTheSame(oldItem: ScheduleItem, newItem: ScheduleItem): Boolean {
                return oldItem.title == newItem.title && oldItem.time == newItem.time
            }

            override fun areContentsTheSame(oldItem: ScheduleItem, newItem: ScheduleItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
