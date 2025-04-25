/*
 * Copyright (C) 2024 Samsung Electronics Co., Ltd. All rights reserved
 */
package com.example.nogorok.utils

import com.samsung.android.sdk.health.data.request.DataType.NutritionType.MealType

class DailyIntakeCalories {
    var undefined = 0f
    var breakfast = 0f
    var lunch = 0f
    var dinner = 0f
    var morningSnack = 0f
    var afternoonSnack = 0f
    var eveningSnack = 0f
    var breakfastNutritionInfo = NutritionInfo()
    var lunchNutritionInfo = NutritionInfo()
    var dinnerNutritionInfo = NutritionInfo()
    var morningSnackNutritionInfo = NutritionInfo()
    var afternoonSnackNutritionInfo = NutritionInfo()
    var eveningSnackNutritionInfo = NutritionInfo()

    fun addData(
        mealType: MealType,
        value: Float,
        titleData: String,
        proteinData: Float,
        totalFatData: Float,
        saturatedFatData: Float,
        polySaturatedFatData: Float,
        monoSaturatedFatData: Float,
        transFatData: Float,
        carbohydrateData: Float,
        dietaryFiberData: Float,
        sugarData: Float,
        cholesterolData: Float,
        sodiumData: Float,
        potassiumData: Float,
        vitaminAData: Float,
        vitaminCData: Float,
        calciumData: Float,
        ironData: Float,
        caloriesData: Float,
    ) {
        when (mealType) {
            MealType.UNDEFINED -> {
                undefined += value
            }

            MealType.BREAKFAST -> {
                breakfast += value
                updateFields(
                    breakfastNutritionInfo, titleData, proteinData, totalFatData,
                    saturatedFatData, polySaturatedFatData, monoSaturatedFatData, transFatData,
                    carbohydrateData, dietaryFiberData, sugarData, cholesterolData, sodiumData,
                    potassiumData, vitaminAData, vitaminCData, calciumData, ironData, caloriesData
                )
            }

            MealType.LUNCH -> {
                lunch += value
                updateFields(
                    lunchNutritionInfo, titleData, proteinData, totalFatData,
                    saturatedFatData, polySaturatedFatData, monoSaturatedFatData, transFatData,
                    carbohydrateData, dietaryFiberData, sugarData, cholesterolData, sodiumData,
                    potassiumData, vitaminAData, vitaminCData, calciumData, ironData, caloriesData
                )
            }

            MealType.MORNING_SNACK -> {
                morningSnack += value
                updateFields(
                    morningSnackNutritionInfo, titleData, proteinData, totalFatData,
                    saturatedFatData, polySaturatedFatData, monoSaturatedFatData, transFatData,
                    carbohydrateData, dietaryFiberData, sugarData, cholesterolData, sodiumData,
                    potassiumData, vitaminAData, vitaminCData, calciumData, ironData, caloriesData
                )
            }

            MealType.AFTERNOON_SNACK -> {
                afternoonSnack += value
                updateFields(
                    afternoonSnackNutritionInfo, titleData, proteinData, totalFatData,
                    saturatedFatData, polySaturatedFatData, monoSaturatedFatData, transFatData,
                    carbohydrateData, dietaryFiberData, sugarData, cholesterolData, sodiumData,
                    potassiumData, vitaminAData, vitaminCData, calciumData, ironData, caloriesData
                )
            }

            MealType.EVENING_SNACK -> {
                eveningSnack += value
                updateFields(
                    eveningSnackNutritionInfo, titleData, proteinData, totalFatData,
                    saturatedFatData, polySaturatedFatData, monoSaturatedFatData, transFatData,
                    carbohydrateData, dietaryFiberData, sugarData, cholesterolData,
                    sodiumData, potassiumData, vitaminAData, vitaminCData, calciumData, ironData,
                    caloriesData
                )
            }

            MealType.DINNER -> {
                dinner += value
                updateFields(
                    dinnerNutritionInfo, titleData, proteinData, totalFatData,
                    saturatedFatData, polySaturatedFatData, monoSaturatedFatData, transFatData,
                    carbohydrateData, dietaryFiberData, sugarData, cholesterolData, sodiumData,
                    potassiumData, vitaminAData, vitaminCData, calciumData, ironData, caloriesData
                )
            }
        }
    }

    data class NutritionInfo(
        var title: String = "",
        var protein: Float = 0F,
        var totalFat: Float = 0F,
        var saturatedFat: Float = 0F,
        var polySaturatedFat: Float = 0F,
        var monoSaturatedFat: Float = 0F,
        var transFat: Float = 0F,
        var carbohydrate: Float = 0F,
        var dietaryFiber: Float = 0F,
        var sugar: Float = 0F,
        var cholesterol: Float = 0F,
        var sodium: Float = 0F,
        var potassium: Float = 0F,
        var vitaminA: Float = 0F,
        var vitaminC: Float = 0F,
        var calcium: Float = 0F,
        var iron: Float = 0F,
        var calories: Float = 0F,
    )

    private fun updateFields(
        nutritionInfo: NutritionInfo,
        titleData: String,
        proteinData: Float,
        totalFatData: Float,
        saturatedFatData: Float,
        polySaturatedFatData: Float,
        monoSaturatedFatData: Float,
        transFatData: Float,
        carbohydrateData: Float,
        dietaryFiberData: Float,
        sugarData: Float,
        cholesterolData: Float,
        sodiumData: Float,
        potassiumData: Float,
        vitaminAData: Float,
        vitaminCData: Float,
        calciumData: Float,
        ironData: Float,
        caloriesData: Float,
    ) {
        nutritionInfo.apply {
            if (titleData.isNotEmpty()) {
                nutritionInfo.title += "$titleData, "
            }
            nutritionInfo.protein += proteinData
            nutritionInfo.totalFat += totalFatData
            nutritionInfo.saturatedFat += saturatedFatData
            nutritionInfo.polySaturatedFat += polySaturatedFatData
            nutritionInfo.monoSaturatedFat += monoSaturatedFatData
            nutritionInfo.transFat += transFatData
            nutritionInfo.carbohydrate += carbohydrateData
            nutritionInfo.dietaryFiber += dietaryFiberData
            nutritionInfo.sugar += sugarData
            nutritionInfo.cholesterol += cholesterolData
            nutritionInfo.sodium += sodiumData
            nutritionInfo.potassium += potassiumData
            nutritionInfo.vitaminA += vitaminAData
            nutritionInfo.vitaminC += vitaminCData
            nutritionInfo.calcium += calciumData
            nutritionInfo.iron += ironData
            nutritionInfo.calories += caloriesData
        }
    }
}
