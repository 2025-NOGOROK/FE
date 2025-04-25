package com.example.nogorok.utils

import android.content.Intent
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.DataType.NutritionType
import com.samsung.android.sdk.health.data.request.DataType.NutritionType.MealType
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

fun getMealType(intent: Intent): MealType {
    val meal = intent.getIntExtra(AppConstants.BUNDLE_KEY_MEAL_TYPE, MealType.UNDEFINED.ordinal)
    return when (meal) {
        0 -> MealType.UNDEFINED
        1 -> MealType.BREAKFAST
        2 -> MealType.LUNCH
        3 -> MealType.DINNER
        4 -> MealType.MORNING_SNACK
        5 -> MealType.AFTERNOON_SNACK
        6 -> MealType.EVENING_SNACK
        else -> {
            MealType.UNDEFINED
        }
    }
}

fun getTime(intent: Intent): String = intent.getStringExtra(AppConstants.BUNDLE_KEY_INSERT_DATE)!!

fun createNutritionDataPoint(
    foodName: String,
    amount: Float,
    mMealType: MealType,
    time: String
): HealthDataPoint {
    val foodData = FoodInfoTable[foodName]
    val sTime = LocalDateTime.parse(time).atZone(ZoneId.systemDefault()).toInstant()
    val eTime = sTime.plus(1, ChronoUnit.DAYS)
    return HealthDataPoint.builder()
        .setStartTime(sTime)
        .setEndTime(eTime)
        .addFieldData(NutritionType.CALCIUM, foodData?.calcium?.times(amount))
        .addFieldData(NutritionType.CALORIES, foodData?.calories?.times(amount))
        .addFieldData(NutritionType.MEAL_TYPE, mMealType)
        .addFieldData(NutritionType.TITLE, foodName)
        .addFieldData(NutritionType.TOTAL_FAT, foodData?.totalFat?.times(amount))
        .addFieldData(NutritionType.SATURATED_FAT, foodData?.saturatedFat?.times(amount))
        .addFieldData(
            NutritionType.POLYSATURATED_FAT,
            foodData?.polySaturatedFat?.times(amount)
        )
        .addFieldData(
            NutritionType.MONOSATURATED_FAT,
            foodData?.monoSaturatedFat?.times(amount)
        )
        .addFieldData(NutritionType.TRANS_FAT, foodData?.transFat?.times(amount))
        .addFieldData(NutritionType.CARBOHYDRATE, foodData?.carbohydrate?.times(amount))
        .addFieldData(NutritionType.DIETARY_FIBER, foodData?.dietaryFiber?.times(amount))
        .addFieldData(NutritionType.SUGAR, foodData?.sugar?.times(amount))
        .addFieldData(NutritionType.PROTEIN, foodData?.protein?.times(amount))
        .addFieldData(NutritionType.CHOLESTEROL, foodData?.cholesterol?.times(amount))
        .addFieldData(NutritionType.SODIUM, foodData?.sodium?.times(amount))
        .addFieldData(NutritionType.POTASSIUM, foodData?.potassium?.times(amount))
        .addFieldData(NutritionType.VITAMIN_A, foodData?.vitaminA?.times(amount))
        .addFieldData(NutritionType.VITAMIN_C, foodData?.vitaminC?.times(amount))
        .addFieldData(NutritionType.CALCIUM, foodData?.calcium?.times(amount))
        .addFieldData(NutritionType.IRON, foodData?.iron?.times(amount))
        .build()
}
