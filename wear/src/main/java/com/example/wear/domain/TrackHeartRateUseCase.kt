package com.example.wear.domain

import com.example.wear.data.TrackingRepository
import com.example.common.TrackedData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TrackHeartRateUseCase @Inject constructor(
    private val repository: TrackingRepository
) {
    suspend operator fun invoke(): Flow<TrackedData> {
        return repository.startTracking()
    }
}
