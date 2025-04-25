/*
 * Copyright (C) 2024 Samsung Electronics Co., Ltd. All rights reserved
 */
package com.example.nogorok.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nogorok.databinding.StepListItemBinding
import com.samsung.android.sdk.health.data.data.AggregatedData

class StepAdapter : RecyclerView.Adapter<StepAdapter.ViewHolder>() {

    private var stepsList: List<AggregatedData<Long>> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            StepListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stepList = stepsList[position]
        val stepTime = "${stepList.getStartLocalDateTime().toLocalTime()} - ${
            stepList.getEndLocalDateTime().toLocalTime()
        }"
        holder.binding.run {
            timeValue.text = stepTime
            stepsValue.text = stepList.value.toString()
        }
    }

    override fun getItemCount(): Int {
        return stepsList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(stepsData: List<AggregatedData<Long>>) {
        stepsList = stepsData
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: StepListItemBinding) : RecyclerView.ViewHolder(binding.root)
}
