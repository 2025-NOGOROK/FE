package com.example.nogorok.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nogorok.R
import com.example.nogorok.databinding.HeartRateListBinding
import com.example.nogorok.utils.formatString
import com.example.nogorok.model.HeartRateViewModel

class HeartRateAdapter : RecyclerView.Adapter<HeartRateAdapter.ViewHolder>() {

    private val heartRateList = mutableListOf<HeartRateViewModel.HeartRate>()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HeartRateListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val heartRate = heartRateList[position]

        with(holder.binding) {
            // 시간 표시
            heartRateTime.text = "${heartRate.startTime} - ${heartRate.endTime}"
            // 평균 심박수
            heartRateValue.text = formatString(heartRate.avg)

            // 최대 심박수
            maxHeartRateValue.text = if (heartRate.max != 0f) {
                "${heartRate.max.toInt()}${context.getString(R.string.heart_rate_unit)}"
            } else {
                context.getString(R.string.no_data)
            }

            // 최소 심박수
            minHeartRateValue.text = if (heartRate.min != 1000f) {
                "${heartRate.min.toInt()}${context.getString(R.string.heart_rate_unit)}"
            } else {
                context.getString(R.string.no_data)
            }
        }
    }

    override fun getItemCount(): Int = heartRateList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(data: List<HeartRateViewModel.HeartRate>) {
        heartRateList.clear()
        heartRateList.addAll(data)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: HeartRateListBinding) :
        RecyclerView.ViewHolder(binding.root)
}
