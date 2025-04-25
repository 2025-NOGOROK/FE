/*
 * Copyright (C) 2024 Samsung Electronics Co., Ltd. All rights reserved
 */
package com.example.nogorok.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nogorok.utils.AppConstants
import com.example.nogorok.utils.DailyIntakeCalories
import com.example.nogorok.utils.FoodInfoTable
import com.example.nogorok.utils.NutritionUpdateData
import com.example.nogorok.utils.dateFormat
import com.example.nogorok.utils.getExceptionHandler
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.permission.AccessType
import com.samsung.android.sdk.health.data.permission.Permission
import com.samsung.android.sdk.health.data.request.DataType.NutritionType
import com.samsung.android.sdk.health.data.request.DataType.NutritionType.MealType
import com.samsung.android.sdk.health.data.request.DataTypes
import com.samsung.android.sdk.health.data.request.LocalTimeFilter
import com.samsung.android.sdk.health.data.request.Ordering
import java.time.Instant
import java.time.LocalDateTime
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NutritionViewModel(private val healthDataStore: HealthDataStore, activity: Activity) :
    ViewModel() {

    private val _permissionResponse = MutableLiveData<Int>()
    private val _exceptionResponse: MutableLiveData<String> = MutableLiveData<String>()
    private val _dailyIntakeCaloriesData = MutableLiveData<DailyIntakeCalories>()
    private val exceptionHandler = getExceptionHandler(activity, _exceptionResponse)
    val permissionResponse: LiveData<Int> = _permissionResponse
    val nutritionTitleData = mutableMapOf<MealType, ArrayList<NutritionUpdateData>>()
    val exceptionResponse: LiveData<String> = _exceptionResponse
    val dailyIntakeCaloriesData: LiveData<DailyIntakeCalories> = _dailyIntakeCaloriesData
    val totalCaloriesCount = ObservableField<String>()
    val dayStartTimeAsText = ObservableField<String>()

    fun readNutritionData(dateTime: LocalDateTime) {
        dayStartTimeAsText.set(dateTime.format(dateFormat))

        val readRequest = DataTypes.NUTRITION.readDataRequestBuilder
            .setLocalTimeFilter(LocalTimeFilter.of(dateTime, dateTime.plusDays(1)))
            .setOrdering(Ordering.ASC)
            .build()

        /**  Make SDK call to read nutrition data */
        viewModelScope.launch(AppConstants.SCOPE_IO_DISPATCHERS + exceptionHandler) {
            val intakeList = healthDataStore.readData(readRequest).dataList
            processReadDataResponse(intakeList)
        }
    }

    private fun processReadDataResponse(intakeList: List<HealthDataPoint>) {
        var totalCalories = 0f
        nutritionTitleData.clear()
        val dailyIntakeCalories = DailyIntakeCalories()

        intakeList.forEach { nutritionData ->
            totalCalories += nutritionData.getValueOrDefault(NutritionType.CALORIES, 0F)
            val mealType =
                nutritionData.getValueOrDefault(NutritionType.MEAL_TYPE, MealType.BREAKFAST)
            val title = nutritionData.getValueOrDefault(NutritionType.TITLE, "")
            val calories = nutritionData.getValueOrDefault(NutritionType.CALORIES, 0F)
            val amount = (calories / (FoodInfoTable[title]?.calories ?: 1F))

            dailyIntakeCalories.addData(
                mealType,
                nutritionData.getValueOrDefault(NutritionType.CALORIES, 0F),
                title,
                nutritionData.getValueOrDefault(NutritionType.PROTEIN, 0F),
                nutritionData.getValueOrDefault(NutritionType.TOTAL_FAT, 0F),
                nutritionData.getValueOrDefault(NutritionType.SATURATED_FAT, 0F),
                nutritionData.getValueOrDefault(NutritionType.POLYSATURATED_FAT, 0F),
                nutritionData.getValueOrDefault(NutritionType.MONOSATURATED_FAT, 0F),
                nutritionData.getValueOrDefault(NutritionType.TRANS_FAT, 0F),
                nutritionData.getValueOrDefault(NutritionType.CARBOHYDRATE, 0F),
                nutritionData.getValueOrDefault(NutritionType.DIETARY_FIBER, 0F),
                nutritionData.getValueOrDefault(NutritionType.SUGAR, 0F),
                nutritionData.getValueOrDefault(NutritionType.CHOLESTEROL, 0F),
                nutritionData.getValueOrDefault(NutritionType.SODIUM, 0F),
                nutritionData.getValueOrDefault(NutritionType.POTASSIUM, 0F),
                nutritionData.getValueOrDefault(NutritionType.VITAMIN_A, 0F),
                nutritionData.getValueOrDefault(NutritionType.VITAMIN_C, 0F),
                nutritionData.getValueOrDefault(NutritionType.CALCIUM, 0F),
                nutritionData.getValueOrDefault(NutritionType.IRON, 0F),
                calories
            )
            if ((nutritionData.dataSource?.appId ?: "") == AppConstants.APP_ID) {
                addNutritionParcel(
                    mealType,
                    title,
                    nutritionData.uid,
                    amount,
                    nutritionData.updateTime,
                    true
                )
            } else {
                addNutritionParcel(
                    mealType,
                    title,
                    nutritionData.uid,
                    amount,
                    nutritionData.updateTime,
                    false
                )
            }
        }

        totalCaloriesCount.set(totalCalories.format())
        _dailyIntakeCaloriesData.postValue(dailyIntakeCalories)
    }

    private fun addNutritionParcel(
        mealType: MealType,
        title: String,
        uid: String,
        amount: Float,
        updateTime: Instant?,
        isClient: Boolean
    ) {
        nutritionTitleData[mealType]?.add(
            NutritionUpdateData(title, uid, amount, updateTime, isClient)
        ) ?: run {
            nutritionTitleData[mealType] = arrayListOf(
                NutritionUpdateData(title, uid, amount, updateTime, isClient)
            )
        }
    }

    fun requestWritePermission(context: Context, appId: Int) {
        val permSet = mutableSetOf(
            Permission.of(DataTypes.NUTRITION, AccessType.WRITE)
        )

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val grantedPermissions = healthDataStore.getGrantedPermissions(permSet)

            if (grantedPermissions.containsAll(permSet)) {
                _permissionResponse.postValue(appId)
            } else {
                requestForPermission(context, permSet, appId)
            }
        }
    }

    private fun requestForPermission(
        context: Context,
        permSet: MutableSet<Permission>,
        appId: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val activity = context as Activity
            val result = healthDataStore.requestPermissions(permSet, activity)
            Log.i(TAG, "requestPermissions: Success ${result.size}")

            if (result.containsAll(permSet)) {
                _permissionResponse.postValue(appId)
            } else {
                withContext(Dispatchers.Main) {
                    _permissionResponse.postValue(AppConstants.NO_WRITE_PERMISSION)
                    Log.i(TAG, "requestPermissions: NO_PERMISSION")
                }
            }
        }
    }

    private fun Float.format() = String.format(Locale.ENGLISH, "%,d", this.toInt())

    companion object {
        private const val TAG = "[HD]NutritionViewModel"
    }
}
