package com.example.nogorok.features.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.HomeResponse
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _stress = MutableLiveData<Float>()
    val stress: LiveData<Float> = _stress

    fun fetchLatestStress() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.homeApi.getLatestStress()
                if (response.isSuccessful) {
                    _stress.value = response.body()?.stress ?: 0f
                } else {
                    _stress.value = 0f
                }
            } catch (e: Exception) {
                _stress.value = 0f
            }
        }
    }
}
