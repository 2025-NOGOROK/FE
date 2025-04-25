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
import com.example.nogorok.StepActivityBinding
import com.example.nogorok.adapters.StepAdapter
import com.example.nogorok.utils.AppConstants.currentDate
import com.example.nogorok.utils.AppConstants.minimumDate
import com.example.nogorok.utils.SwipeDetector
import com.example.nogorok.utils.SwipeDetector.OnSwipeEvent
import com.example.nogorok.utils.SwipeDetector.SwipeTypeEnum
import com.example.nogorok.utils.showDatePickerDialogueBox
import com.example.nogorok.utils.showToast
import com.example.nogorok.viewmodel.HealthViewModelFactory
import com.example.nogorok.viewmodel.StepViewModel

class StepActivity : AppCompatActivity() {

    private lateinit var binding: StepActivityBinding
    private lateinit var stepViewModel: StepViewModel
    private lateinit var stepAdapter: StepAdapter
    private var startDate = currentDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        stepViewModel = ViewModelProvider(
            this, HealthViewModelFactory(this)
        )[StepViewModel::class.java]

        stepAdapter = StepAdapter()

        binding = DataBindingUtil
            .setContentView<StepActivityBinding>(this, R.layout.step)
            .apply {
                viewModel = stepViewModel
                stepsList.layoutManager = LinearLayoutManager(this@StepActivity)
                stepsList.adapter = stepAdapter
            }

        initializeOnClickListeners()
        setSwipeDetector()
        setStepDataObservers()
    }

    private fun initializeOnClickListeners() {
        binding.movePreviousDate.setOnClickListener {
            movePreviousDate()
        }

        binding.moveNextDate.setOnClickListener {
            moveNextDate()
        }

        binding.datePicker.setOnClickListener {
            showDatePickerDialogueBox(this@StepActivity, startDate) { newStartDate ->
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
                stepViewModel.readStepData(startDate)
            }
        }
    }

    private fun setSwipeDetector() {
        SwipeDetector(binding.stepsList).setOnSwipeListener(object : OnSwipeEvent {
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

    private fun setStepDataObservers() {
        /**  Update steps UI */
        stepViewModel.totalStepCountData.observe(this) {
            stepAdapter.updateList(it)
        }

        /** Show toast on exception occurrence **/
        stepViewModel.exceptionResponse.observe(this) { message ->
            showToast(this, message)
        }
    }

    override fun onResume() {
        super.onResume()
        if (startDate == currentDate) {
            binding.moveNextDate.setColorFilter(getColor(R.color.silver))
        }
        stepViewModel.readStepData(startDate)
    }

    private fun movePreviousDate() {
        if (startDate > minimumDate) {
            startDate = startDate.minusDays(1)
            if (startDate == minimumDate) {
                binding.movePreviousDate.setColorFilter(getColor(R.color.silver))
            }
            binding.moveNextDate.setColorFilter(getColor(R.color.black))
            stepViewModel.readStepData(startDate)
        }
    }

    private fun moveNextDate() {
        if (startDate < currentDate) {
            startDate = startDate.plusDays(1)
            if (startDate == currentDate) {
                binding.moveNextDate.setColorFilter(getColor(R.color.silver))
            }
            binding.movePreviousDate.setColorFilter(getColor(R.color.black))
            stepViewModel.readStepData(startDate)
        }
    }
}
