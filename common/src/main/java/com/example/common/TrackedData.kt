package com.example.common

import kotlinx.serialization.Serializable

@Serializable
data class TrackedData(
    var hr: Int = 0,
    var ibi: ArrayList<Int> = ArrayList()
)