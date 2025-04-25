/*
 * Copyright (C) 2024 Samsung Electronics Co., Ltd. All rights reserved
 */
package com.example.nogorok.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nogorok.databinding.SleepSessionItemBinding
import com.samsung.android.sdk.health.data.data.entries.SleepSession
import java.time.LocalDateTime
import java.time.ZoneOffset

class SleepSessionAdapter : RecyclerView.Adapter<SleepSessionAdapter.ViewHolder>() {

    private var sleepSessionList: List<SleepSession> = emptyList()
    private var zoneOffset: ZoneOffset? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SleepSessionAdapter.ViewHolder {
        val binding =
            SleepSessionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sleepSession = sleepSessionList[position]
        val timeInBed = "${
            LocalDateTime.ofInstant(sleepSession.startTime, zoneOffset).toLocalTime()
        } - ${
            LocalDateTime.ofInstant(sleepSession.endTime, zoneOffset).toLocalTime()
        }"
        holder.binding.timeInBed.text = timeInBed
    }

    override fun getItemCount(): Int {
        return sleepSessionList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(sleepSessionData: List<SleepSession>, sleepZoneOffset: ZoneOffset?) {
        sleepSessionList = sleepSessionData
        zoneOffset = sleepZoneOffset
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: SleepSessionItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
