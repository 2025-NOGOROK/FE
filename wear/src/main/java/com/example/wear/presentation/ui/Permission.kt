package com.example.wear.presentation.ui

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.wear.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.PermissionChecker
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Permission(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val permissions = mutableListOf(
        Manifest.permission.BODY_SENSORS,
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
    }

    val notGranted = permissions.filter {
        ContextCompat.checkSelfPermission(context, it) != PermissionChecker.PERMISSION_GRANTED
    }

    if (notGranted.isEmpty()) {
        content()
    } else {
        LaunchedEffect(Unit) {
            ActivityCompat.requestPermissions(
                context as android.app.Activity,
                notGranted.toTypedArray(),
                0
            )
        }
        Text("권한이 필요합니다")
    }
}
