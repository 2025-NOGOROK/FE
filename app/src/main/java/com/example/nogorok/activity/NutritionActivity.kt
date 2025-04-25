/*
 * Copyright (C) 2024 Samsung Electronics Co., Ltd. All rights reserved
 */
package com.example.nogorok.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.nogorok.NutritionActivityBinding
import com.example.nogorok.R
import com.example.nogorok.databinding.NutritionInfoBinding
import com.example.nogorok.utils.AppConstants
import com.example.nogorok.utils.AppConstants.currentDate
import com.example.nogorok.utils.AppConstants.minimumDate
import com.example.nogorok.utils.DailyIntakeCalories
import com.example.nogorok.utils.SwipeDetector
import com.example.nogorok.utils.SwipeDetector.OnSwipeEvent
import com.example.nogorok.utils.SwipeDetector.SwipeTypeEnum
import com.example.nogorok.utils.formatString
import com.example.nogorok.utils.showDatePickerDialogueBox
import com.example.nogorok.utils.showToast
import com.example.nogorok.viewmodel.HealthViewModelFactory
import com.example.nogorok.viewmodel.NutritionViewModel
import com.samsung.android.sdk.health.data.request.DataType.NutritionType.MealType
import java.util.Locale

class NutritionActivity : AppCompatActivity() {

    private lateinit var binding: NutritionActivityBinding
    private lateinit var nutritionViewModel: NutritionViewModel
    private lateinit var mMealType: MealType
    private var startDate = currentDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nutritionViewModel = ViewModelProvider(
            this, HealthViewModelFactory(this)
        )[NutritionViewModel::class.java]

        binding = DataBindingUtil
            .setContentView<NutritionActivityBinding>(this, R.layout.nutrition)
            .apply {
                viewModel = nutritionViewModel
            }

        initializeOnClickListeners()
        setSwipeDetector()
        setNutritionDataObservers()
    }

    private fun initializeOnClickListeners() {
        binding.movePreviousDate.setOnClickListener {
            movePreviousDate()
        }

        binding.moveNextDate.setOnClickListener {
            moveNextDate()
        }

        binding.datePicker.setOnClickListener {
            showDatePickerDialogueBox(this@NutritionActivity, startDate) { newStartDate ->
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
                nutritionViewModel.readNutritionData(startDate)
            }
        }

        binding.breakfastAdd.setOnClickListener {
            requestWritePermissionApi(MealType.BREAKFAST)
        }

        binding.morningSnackAdd.setOnClickListener {
            requestWritePermissionApi(MealType.MORNING_SNACK)
        }

        binding.lunchAdd.setOnClickListener {
            requestWritePermissionApi(MealType.LUNCH)
        }

        binding.afternoonSnackAdd.setOnClickListener {
            requestWritePermissionApi(MealType.AFTERNOON_SNACK)
        }
        binding.eveningSnackAdd.setOnClickListener {
            requestWritePermissionApi(MealType.EVENING_SNACK)
        }
        binding.dinnerAdd.setOnClickListener {
            requestWritePermissionApi(MealType.DINNER)
        }

        binding.breakfastUpdate.setOnClickListener {
            checkUpdateCondition(
                nutritionViewModel.nutritionTitleData[MealType.BREAKFAST].isNullOrEmpty(),
                MealType.BREAKFAST
            )
        }
        binding.morningSnackUpdate.setOnClickListener {
            checkUpdateCondition(
                nutritionViewModel.nutritionTitleData[MealType.MORNING_SNACK].isNullOrEmpty(),
                MealType.MORNING_SNACK
            )
        }
        binding.lunchUpdate.setOnClickListener {
            checkUpdateCondition(
                nutritionViewModel.nutritionTitleData[MealType.LUNCH].isNullOrEmpty(),
                MealType.LUNCH
            )
        }
        binding.afternoonSnackUpdate.setOnClickListener {
            checkUpdateCondition(
                nutritionViewModel.nutritionTitleData[MealType.AFTERNOON_SNACK].isNullOrEmpty(),
                MealType.AFTERNOON_SNACK
            )
        }
        binding.eveningSnackUpdate.setOnClickListener {
            checkUpdateCondition(
                nutritionViewModel.nutritionTitleData[MealType.EVENING_SNACK].isNullOrEmpty(),
                MealType.EVENING_SNACK
            )
        }
        binding.dinnerUpdate.setOnClickListener {
            checkUpdateCondition(
                nutritionViewModel.nutritionTitleData[MealType.DINNER].isNullOrEmpty(),
                MealType.DINNER
            )
        }
    }

    private fun requestWritePermissionApi(mealType: MealType) {
        mMealType = mealType
        nutritionViewModel.requestWritePermission(this, AppConstants.CHOOSE_FOOD_ACTIVITY)
    }

    private fun checkUpdateCondition(isNoData: Boolean, mealType: MealType) {
        if (isNoData) {
            showToast(this, this.getString(R.string.update_no_data))
        } else {
            mMealType = mealType
            nutritionViewModel.requestWritePermission(this, AppConstants.UPDATE_FOOD_ACTIVITY)
        }
    }

    private fun setSwipeDetector() {
        SwipeDetector(binding.scrollView).setOnSwipeListener(object : OnSwipeEvent {
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

    private fun setNutritionDataObservers() {
        /**  Update nutrition UI */
        nutritionViewModel.dailyIntakeCaloriesData.observe(this) {
            updateNutritionUI(it)
            updateNutritionInfoBox(it)
        }

        /** Show toast on exception occurrence **/
        nutritionViewModel.exceptionResponse.observe(this) { message ->
            showToast(this, message)
        }

        /** Check for nutrition write permission **/
        nutritionViewModel.permissionResponse.observe(this) {
            when (it) {
                AppConstants.CHOOSE_FOOD_ACTIVITY -> startChooseFoodActivity(mMealType.ordinal)
                AppConstants.UPDATE_FOOD_ACTIVITY -> startUpdateFoodActivity()
                AppConstants.NO_WRITE_PERMISSION -> showToast(this, AppConstants.NO_PERMISSION)
            }
        }
    }

    private fun startChooseFoodActivity(mealInput: Int) {
        val intent = Intent(this, ChooseFoodActivity::class.java)
        intent.putExtra(AppConstants.BUNDLE_KEY_MEAL_TYPE, mealInput)
        intent.putExtra(AppConstants.BUNDLE_KEY_INSERT_DATE, startDate.toString())
        startActivity(intent)
    }

    private fun startUpdateFoodActivity() {
        val intent = Intent(this, UpdateFoodActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelableArrayList(
            AppConstants.BUNDLE_KEY_NUTRITION_DATA,
            nutritionViewModel.nutritionTitleData[mMealType]
        )
        intent.putExtra(AppConstants.BUNDLE_KEY_MEAL_TYPE, mMealType.ordinal)
        intent.putExtra(AppConstants.BUNDLE_KEY_INSERT_DATE, startDate.toString())
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if (startDate == currentDate) {
            binding.moveNextDate.setColorFilter(getColor(R.color.silver))
        }
        nutritionViewModel.readNutritionData(startDate)
    }

    private fun movePreviousDate() {
        if (startDate > minimumDate) {
            startDate = startDate.minusDays(1)
            if (startDate == minimumDate) {
                binding.movePreviousDate.setColorFilter(getColor(R.color.silver))
            }
            binding.moveNextDate.setColorFilter(getColor(R.color.black))
            nutritionViewModel.readNutritionData(startDate)
        }
    }

    private fun moveNextDate() {
        if (startDate < currentDate) {
            startDate = startDate.plusDays(1)
            if (startDate == currentDate) {
                binding.moveNextDate.setColorFilter(getColor(R.color.silver))
            }
            binding.movePreviousDate.setColorFilter(getColor(R.color.black))
            nutritionViewModel.readNutritionData(startDate)
        }
    }

    private fun updateNutritionUI(dailyIntakeCalories: DailyIntakeCalories) {
        binding.run {
            breakfast.text = dailyIntakeCalories.breakfast.format()
            morningSnack.text = dailyIntakeCalories.morningSnack.format()
            lunch.text = dailyIntakeCalories.lunch.format()
            afternoonSnack.text = dailyIntakeCalories.afternoonSnack.format()
            eveningSnack.text = dailyIntakeCalories.eveningSnack.format()
            dinner.text = dailyIntakeCalories.dinner.format()
            breakfastData.text =
                titleSlicing(dailyIntakeCalories.breakfastNutritionInfo.title)
            morningSnackData.text =
                titleSlicing(dailyIntakeCalories.morningSnackNutritionInfo.title)
            lunchData.text =
                titleSlicing(dailyIntakeCalories.lunchNutritionInfo.title)
            afternoonSnackData.text =
                titleSlicing(dailyIntakeCalories.afternoonSnackNutritionInfo.title)
            eveningSnackData.text =
                titleSlicing(dailyIntakeCalories.eveningSnackNutritionInfo.title)
            dinnerData.text =
                titleSlicing(dailyIntakeCalories.dinnerNutritionInfo.title)
        }
    }

    private fun updateNutritionInfoBox(dailyIntakeCalories: DailyIntakeCalories) {
        binding.breakfastBox.setOnClickListener { _ ->
            showNutritionBox(dailyIntakeCalories.breakfastNutritionInfo)
        }
        binding.morningSnackBox.setOnClickListener { _ ->
            showNutritionBox(dailyIntakeCalories.morningSnackNutritionInfo)
        }
        binding.lunchBox.setOnClickListener { _ ->
            showNutritionBox(dailyIntakeCalories.lunchNutritionInfo)
        }
        binding.afternoonSnackBox.setOnClickListener { _ ->
            showNutritionBox(dailyIntakeCalories.afternoonSnackNutritionInfo)
        }
        binding.eveningSnackBox.setOnClickListener { _ ->
            showNutritionBox(dailyIntakeCalories.eveningSnackNutritionInfo)
        }
        binding.dinnerBox.setOnClickListener { _ ->
            showNutritionBox(dailyIntakeCalories.dinnerNutritionInfo)
        }
    }

    private fun showNutritionBox(nutritionInfo: DailyIntakeCalories.NutritionInfo) {
        val customBinding = NutritionInfoBinding.inflate(LayoutInflater.from(this))

        customBinding.let {
            it.proteinData.text = formatString(nutritionInfo.protein)
            it.totalFatData.text = formatString(nutritionInfo.totalFat)
            it.saturatedFatData.text = formatString(nutritionInfo.saturatedFat)
            it.polySaturatedFatData.text = formatString(nutritionInfo.polySaturatedFat)
            it.monoSaturatedFatData.text = formatString(nutritionInfo.monoSaturatedFat)
            it.transFatData.text = formatString(nutritionInfo.transFat)
            it.carbohydrateData.text = formatString(nutritionInfo.carbohydrate)
            it.dietaryFiberData.text = formatString(nutritionInfo.dietaryFiber)
            it.sugarData.text = formatString(nutritionInfo.sugar)
            it.cholesterolData.text = formatString(nutritionInfo.cholesterol)
            it.sodiumData.text = formatString(nutritionInfo.sodium)
            it.potassiumData.text = formatString(nutritionInfo.potassium)
            it.vitaminAData.text = formatString(nutritionInfo.vitaminA)
            it.vitaminCData.text = formatString(nutritionInfo.vitaminC)
            it.calciumData.text = formatString(nutritionInfo.calcium)
            it.ironData.text = formatString(nutritionInfo.iron)
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.nutrition_info)
            .setView(customBinding.root)
            .create()
            .show()
    }

    private fun titleSlicing(title: String): String = title.slice(0..(title.length - 3))

    private fun Float.format() = String.format(Locale.ENGLISH, "%,d", this.toInt())
}
