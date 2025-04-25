/*
 * Copyright (C) 2024 Samsung Electronics Co., Ltd. All rights reserved
 */
package com.example.nogorok.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nogorok.R
import com.example.nogorok.databinding.HeartRateListBinding
import com.example.nogorok.utils.formatString
import com.example.nogorok.viewmodel.HeartRateViewModel

class HeartRateAdapter : RecyclerView.Adapter<HeartRateAdapter.ViewHolder>() {

    private var heartRateList: MutableList<HeartRateViewModel.HeartRate> = mutableListOf()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeartRateAdapter.ViewHolder {
        val binding =
            HeartRateListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val heartRate = heartRateList[position]
        val heartRateTime = "${heartRate.startTime} - ${heartRate.endTime}"
        val stressValue = "${heartRate.stress.toInt()}%"

        holder.binding.run {
            this.heartRateTime.text = heartRateTime
            this.heartRateValue.text = formatString(heartRate.avg)
            this.stressValue.text = stressValue
        }
        if (heartRate.max != 0f) {
            val maxHeartRate =
                heartRate.max.toInt().toString() + context.getString(R.string.heart_rate_unit)
            holder.binding.maxHeartRateValue.text = maxHeartRate
        } else {
            holder.binding.maxHeartRateValue.text = context.getString(R.string.no_data)
        }
        if (heartRate.min != 1000f) {
            val minHeartRate =
                heartRate.min.toInt().toString() + context.getString(R.string.heart_rate_unit)
            holder.binding.minHeartRateValue.text = minHeartRate
        } else {
            holder.binding.minHeartRateValue.text = context.getString(R.string.no_data)
        }
    }

    override fun getItemCount(): Int {
        return heartRateList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(heartRateData: List<HeartRateViewModel.HeartRate>) {
        heartRateList.clear()
        heartRateList.addAll(heartRateData)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: HeartRateListBinding) :
        RecyclerView.ViewHolder(binding.root)
}
