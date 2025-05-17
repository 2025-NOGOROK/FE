/*
 * Copyright (C) 2024 Samsung Electronics Co., Ltd. All rights reserved
 */
package com.example.nogorok.features.connect.health.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.nogorok.R
import com.samsung.android.sdk.health.data.error.ResolvablePlatformException
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.CoroutineExceptionHandler

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

/** Common Exception Handler (Resolve ResolvablePlatformException) **/
fun getExceptionHandler(
    activity: Activity,
    exceptionResponse: MutableLiveData<String>
): CoroutineExceptionHandler {
    return CoroutineExceptionHandler { _, exception ->
        if ((exception is ResolvablePlatformException) && exception.hasResolution) {
            exception.resolve(activity)
        }
        exceptionResponse.postValue(exception.message!!)
    }
}

fun showDatePickerDialogueBox(
    activity: Activity,
    startDate: LocalDateTime,
    callback: (LocalDateTime) -> Unit
) {
    var newStartDate = startDate
    val customLayout: View = activity.layoutInflater.inflate(R.layout.calender_view, null)
    val selectDatePicker = customLayout.findViewById<DatePicker>(R.id.date_picker)
    selectDatePicker.maxDate = Calendar.getInstance().timeInMillis

    selectDatePicker.init(
        startDate.year,
        startDate.monthValue - 1,
        startDate.dayOfMonth
    ) { _, year, month, day ->
        val actualMonth = month + 1
        val updatedMonth = if (actualMonth < 10) "0$actualMonth" else actualMonth
        val updatedDay = if (day < 10) "0$day" else day
        newStartDate = LocalDateTime.parse(
            "$year-$updatedMonth-$updatedDay 00:00",
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.US)
        )
    }

    AlertDialog.Builder(activity)
        .setTitle(R.string.date_picker_title)
        .setView(customLayout)
        .setPositiveButton(R.string.date_picker_confirm_button) { _, _ ->
            callback(newStartDate)
        }
        .create()
        .show()
}

fun formatString(input: Float): String {
    return String.format(Locale.ENGLISH, "%.2f", input)
}

val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy (E)")
    .withZone(ZoneId.systemDefault())
