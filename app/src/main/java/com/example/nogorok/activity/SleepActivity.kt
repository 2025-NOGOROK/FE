/*
 * Copyright (C) 2024 Samsung Electronics Co., Ltd. All rights reserved
 */
package com.example.nogorok.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nogorok.R
import com.example.nogorok.SleepActivityBinding
import com.example.nogorok.adapters.SleepAdapter
import com.example.nogorok.utils.AppConstants.currentDate
import com.example.nogorok.utils.AppConstants.minimumDate
import com.example.nogorok.utils.SwipeDetector
import com.example.nogorok.utils.SwipeDetector.OnSwipeEvent
import com.example.nogorok.utils.SwipeDetector.SwipeTypeEnum
import com.example.nogorok.utils.showDatePickerDialogueBox
import com.example.nogorok.utils.showToast
import com.example.nogorok.viewmodel.HealthViewModelFactory
import com.example.nogorok.viewmodel.SleepViewModel

class SleepActivity : AppCompatActivity() {

    private lateinit var sleepViewModel: SleepViewModel
    private lateinit var binding: SleepActivityBinding
    private lateinit var sleepAdapter: SleepAdapter
    private var startDate = currentDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sleepViewModel = ViewModelProvider(
            this, HealthViewModelFactory(this)
        )[SleepViewModel::class.java]

        sleepAdapter = SleepAdapter()

        binding = DataBindingUtil
            .setContentView<SleepActivityBinding>(this, R.layout.sleep)
            .apply {
                viewModel = sleepViewModel
                sleepList.layoutManager = LinearLayoutManager(this@SleepActivity)
                sleepList.adapter = sleepAdapter
            }

        initializeOnClickListeners()
        setSwipeDetector()
        setSleepDataObservers()
    }

    private fun initializeOnClickListeners() {
        binding.movePreviousDate.setOnClickListener {
            movePreviousDate()
        }

        binding.moveNextDate.setOnClickListener {
            moveNextDate()
        }

        binding.datePicker.setOnClickListener {
            showDatePickerDialogueBox(this@SleepActivity, startDate) { newStartDate ->
                startDate = newStartDate
                if (startDate == minimumDate) {
                    binding.movePreviousDate.setColorFilter(getColor(R.color.silver))
                } else {
                    binding.movePreviousDate.setColorFilter(getColor(R.color.black))
                }
                if (newStartDate == currentDate) {
                    binding.moveNextDate.setColorFilter(getColor(R.color.silver))
                } else {
                    binding.moveNextDate.setColorFilter(getColor(R.color.black))
                }
                sleepViewModel.readSleepData(startDate)
            }
        }
    }

    private fun setSwipeDetector() {
        SwipeDetector(binding.sleepList).setOnSwipeListener(object : OnSwipeEvent {
            override fun swipeEventDetected(
                swipeType: SwipeTypeEnum
            ) {
                if (swipeType == SwipeTypeEnum.LEFT_TO_RIGHT) {
                    movePreviousDate()
                } else if (swipeType == SwipeTypeEnum.RIGHT_TO_LEFT) {
                    moveNextDate()
                }
            }
        })
    }

    private fun setSleepDataObservers() {
        /**  Update sleep UI */
        sleepViewModel.dailySleepData.observe(this) {
            sleepAdapter.updateList(it)
        }

        /**  Update sleep Associate UI */
        sleepViewModel.associatedData.observe(this) {
            sleepAdapter.updateAssociatedList(it)
        }

        /** Show toast on exception occurrence **/
        sleepViewModel.exceptionResponse.observe(this) { message ->
            showToast(this, message)
        }
    }

    override fun onResume() {
        super.onResume()
        if (startDate == currentDate) {
            binding.moveNextDate.setColorFilter(getColor(R.color.silver))
        }
        sleepViewModel.readSleepData(startDate)
    }

    private fun movePreviousDate() {
        if (startDate > minimumDate) {
            startDate = startDate.minusDays(1)
            if (startDate == minimumDate) {
                binding.movePreviousDate.setColorFilter(getColor(R.color.silver))
            }
            binding.moveNextDate.setColorFilter(getColor(R.color.black))
            sleepViewModel.readSleepData(startDate)
        }
    }

    private fun moveNextDate() {
        if (startDate < currentDate) {
            startDate = startDate.plusDays(1)
            if (startDate == currentDate) {
                binding.moveNextDate.setColorFilter(getColor(R.color.silver))
            }
            binding.movePreviousDate.setColorFilter(getColor(R.color.black))
            sleepViewModel.readSleepData(startDate)
        }
    }
}
