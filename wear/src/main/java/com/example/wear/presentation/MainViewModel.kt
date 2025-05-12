package com.example.wear.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.TrackedData
import com.example.wear.domain.StopTrackingUseCase
import com.example.wear.domain.TrackHeartRateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val trackHeartRateUseCase: TrackHeartRateUseCase,
    private val stopTrackingUseCase: StopTrackingUseCase
) : ViewModel() {

    private val _trackingData = MutableStateFlow<TrackedData?>(null)
    val trackingData: StateFlow<TrackedData?> = _trackingData

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking

    private val _trackingError = MutableStateFlow<String?>(null)
    val trackingError: StateFlow<String?> = _trackingError

    fun startTracking() {
        viewModelScope.launch {
            trackHeartRateUseCase().collect { data ->
                _trackingData.value = data
            }
        }
        _isTracking.value = true
    }

    fun stopTracking() {
        viewModelScope.launch {
            stopTrackingUseCase()
            _isTracking.value = false
        }
    }
}