package com.example.wear.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wear.presentation.MainViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val data by viewModel.trackingData.collectAsState()
    val isTracking by viewModel.isTracking.collectAsState()
    val error by viewModel.trackingError.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("HR: ${data?.hr ?: "--"}")
        Spacer(modifier = Modifier.height(10.dp))
        Text("IBI: ${data?.ibi?.joinToString(", ") ?: "--"}")
        Spacer(modifier = Modifier.height(10.dp))
        if (error != null) {
            Text("⚠️ $error")
            Spacer(modifier = Modifier.height(10.dp))
        }
        Button(onClick = {
            if (isTracking) viewModel.stopTracking() else viewModel.startTracking()
        }) {
            Text(if (isTracking) "Stop" else "Start")
        }
    }
}
