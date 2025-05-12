package com.example.wear.domain

import com.example.wear.data.TrackingRepository
import javax.inject.Inject

class StopTrackingUseCase @Inject constructor(
    private val repository: TrackingRepository
) {
    suspend operator fun invoke() {
        repository.stopTracking()
    }
}
