package com.example.wear.data

import kotlinx.coroutines.flow.Flow
import com.example.common.TrackedData

interface TrackingRepository {
    fun startTracking(): Flow<TrackedData>
    fun stopTracking()
}

data class TrackingData(
    val heartRate: Int = 0,
    val ibi: List<Long> = emptyList(),
    val rmssd: Double = 0.0,
    val stressLevel: Int = 0
)
