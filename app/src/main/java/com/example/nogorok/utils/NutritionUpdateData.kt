package com.example.nogorok.utils

import android.os.Parcelable
import java.time.Instant
import kotlinx.parcelize.Parcelize

@Parcelize
class NutritionUpdateData(
    val title: String,
    val uid: String,
    val amount: Float,
    val updateTime: Instant?,
    val isClient: Boolean
) : Parcelable
