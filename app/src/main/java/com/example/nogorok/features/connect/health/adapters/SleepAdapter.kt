/*
 * Copyright (C) 2024 Samsung Electronics Co., Ltd. All rights reserved
 */
package com.example.nogorok.features.connect.health.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nogorok.R
import com.example.nogorok.databinding.SleepItemBinding
import com.example.nogorok.features.connect.health.utils.formatString
import com.example.nogorok.features.connect.health.utils.AppConstants
import com.samsung.android.sdk.health.data.data.AssociatedDataPoints
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.data.entries.SleepSession
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.DataTypes
import java.util.Locale
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class SleepAdapter : RecyclerView.Adapter<SleepAdapter.ViewHolder>() {

    private var sleepDataList: MutableList<HealthDataPoint> = mutableListOf()
    private var associatedDataList: MutableList<AssociatedDataPoints> = mutableListOf()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SleepItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.sleepSessionList.layoutManager = LinearLayoutManager(parent.context)
        context = parent.context
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sleepData = sleepDataList[position]
        val sleepSessionAdapter = SleepSessionAdapter()
        holder.binding.run {
            sleepSessionList.adapter = sleepSessionAdapter
            durationDisplay.text = sleepData.getValue(DataType.SleepType.DURATION)?.let {
                durationParsing(it.toString())
            } ?: ""
        }

        val sleepSessionList = sleepData.getValue(DataType.SleepType.SESSIONS) as List<SleepSession>
        sleepSessionAdapter.updateList(sleepSessionList, sleepData.zoneOffset)
        val stage = SleepStage(0, 0, 0, 0)
        sleepSessionList.forEach { sleepSession ->
            sleepSession.stages?.forEach {
                processSleepStages(it, stage)
            }
        }

        holder.binding.run {
            stageAwakeDuration.text = if (stage.awake > 0) {
                stage.awake.toDuration(DurationUnit.SECONDS).toString()
            } else {
                context.getString(R.string.no_data)
            }
            stageLightDuration.text = if (stage.light > 0) {
                stage.light.toDuration(DurationUnit.SECONDS).toString()
            } else {
                context.getString(R.string.no_data)
            }
            stageRemDuration.text = if (stage.rem > 0) {
                stage.rem.toDuration(DurationUnit.SECONDS).toString()
            } else {
                context.getString(R.string.no_data)
            }
            stageDeepDuration.text = if (stage.deep > 0) {
                stage.deep.toDuration(DurationUnit.SECONDS).toString()
            } else {
                context.getString(R.string.no_data)
            }
        }

        if (associatedDataList.isNotEmpty()) {
            processAssociatedData(position, holder)
        }
    }

    override fun getItemCount(): Int {
        return sleepDataList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(sleepData: List<HealthDataPoint>) {
        sleepDataList.clear()
        sleepDataList.addAll(sleepData)
        if (sleepDataList.isEmpty()) {
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAssociatedList(associatedData: List<AssociatedDataPoints>) {
        associatedDataList.clear()
        associatedDataList.addAll(associatedData)
        notifyDataSetChanged()
    }

    data class SleepStage(
        var undefined: Int = 0,
        var awake: Int = 0,
        var light: Int = 0,
        var rem: Int = 0,
        var deep: Int = 0
    )

    private fun processSleepStages(sleepStage: SleepSession.SleepStage, stage: SleepStage) {
        val startTime = sleepStage.startTime.epochSecond
        val endTime = sleepStage.endTime.epochSecond
        stage.apply {
            when (sleepStage.stage) {
                DataType.SleepType.StageType.UNDEFINED -> {
                    stage.undefined += (endTime - startTime).toInt()
                }

                DataType.SleepType.StageType.AWAKE -> {
                    stage.awake += (endTime - startTime).toInt()
                }

                DataType.SleepType.StageType.LIGHT -> {
                    stage.light += (endTime - startTime).toInt()
                }

                DataType.SleepType.StageType.REM -> {
                    stage.rem += (endTime - startTime).toInt()
                }

                DataType.SleepType.StageType.DEEP -> {
                    stage.deep += (endTime - startTime).toInt()
                }
            }
        }
    }

    private fun processAssociatedData(position: Int, holder: ViewHolder) {
        val sleepAssociatedData = associatedDataList[position]
        val skinTempList = sleepAssociatedData.getDataPointOf(DataTypes.SKIN_TEMPERATURE)
        if (!skinTempList.isNullOrEmpty()) {
            val maxSkinTemperature = skinTempList.maxOf {
                it.getValueOrDefault(DataType.SkinTemperatureType.MAX_SKIN_TEMPERATURE, 0F)
            }
            val minSkinTemperature = skinTempList.minOf {
                it.getValueOrDefault(DataType.SkinTemperatureType.MIN_SKIN_TEMPERATURE, 999F)
            }
            val formattedMaxSkinTemperature =
                formatString(maxSkinTemperature) + AppConstants.SKIN_TEMP_UNIT
            val formattedMinSkinTemperature =
                formatString(minSkinTemperature) + AppConstants.SKIN_TEMP_UNIT
            holder.binding.maxSkinTempValue.text = formattedMaxSkinTemperature
            holder.binding.minSkinTempValue.text = formattedMinSkinTemperature
        } else {
            holder.binding.maxSkinTempValue.text = context.getString(R.string.no_data)
            holder.binding.minSkinTempValue.text = context.getString(R.string.no_data)
        }
        val oxygenList = sleepAssociatedData.getDataPointOf(DataTypes.BLOOD_OXYGEN)
        if (!oxygenList.isNullOrEmpty()) {
            val maxOxygenSaturation = oxygenList.maxOf {
                it.getValueOrDefault(DataType.BloodOxygenType.MAX_OXYGEN_SATURATION, 0F)
            }
            val minOxygenSaturation = oxygenList.minOf {
                it.getValueOrDefault(DataType.BloodOxygenType.MIN_OXYGEN_SATURATION, 999F)
            }

            val formattedMaxBloodOxygen =
                maxOxygenSaturation.toInt().toString() + AppConstants.BLOOD_OXYGEN_UNIT
            val formattedMinBloodOxygen =
                minOxygenSaturation.toInt().toString() + AppConstants.BLOOD_OXYGEN_UNIT
            holder.binding.maxOxygenValue.text = formattedMaxBloodOxygen
            holder.binding.minOxygenValue.text = formattedMinBloodOxygen
        } else {
            holder.binding.maxOxygenValue.text = context.getString(R.string.no_data)
            holder.binding.minOxygenValue.text = context.getString(R.string.no_data)
        }
    }

    inner class ViewHolder(
        val binding: SleepItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    private fun durationParsing(s: String): String = s.substring(2)
        .lowercase(Locale.ROOT).replace(Regex("[hms](?!\$)")) { it.value + " " }
}
