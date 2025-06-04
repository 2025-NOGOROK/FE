package com.example.nogorok.features.auth.register.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.nogorok.R
import com.example.nogorok.features.auth.fcm.FCMTokenManager
import com.example.nogorok.features.auth.register.RegisterActivity
import com.example.nogorok.features.auth.register.RegisterViewModel
import com.example.nogorok.network.RetrofitClient
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class NotificationFragment : Fragment() {

    private lateinit var btnNext: MaterialButton
    private lateinit var viewModel: RegisterViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.notificationAgreed = isGranted
        btnNext.isEnabled = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_register_input_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(requireActivity())[RegisterViewModel::class.java]

        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        btnNext = view.findViewById(R.id.btnNext)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnNext.isEnabled = false
        btnNext.setOnClickListener {
            (activity as? RegisterActivity)?.navigateToNext("Notification")
        }

        checkAndRequestNotificationPermission()
        fetchFcmTokenAndRegister()
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            )

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                viewModel.notificationAgreed = true
                btnNext.isEnabled = true
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            viewModel.notificationAgreed = true
            btnNext.isEnabled = true
        }
    }

    private fun fetchFcmTokenAndRegister() {
        FCMTokenManager.fetchToken(
            context = requireContext(),
            onTokenReceived = { token ->
                viewModel.deviceToken = token
                Log.d("NotificationFragment", "FCM Token 저장 완료: $token")
            },
            onError = { error ->
                Log.e("NotificationFragment", "FCM Token 수신 실패: ${error.message}")
            }
        )
    }
}