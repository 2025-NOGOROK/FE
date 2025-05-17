package com.example.nogorok.features.auth.register

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.nogorok.R
import com.example.nogorok.features.auth.register.fragments.*

class RegisterActivity : AppCompatActivity() {

    lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        // ✅ 약관 동의 여부를 Intent로부터 받아 ViewModel에 저장
        intent.extras?.let { bundle ->
            viewModel.agreedService = bundle.getBoolean("AGREED_SERVICE", false)
            viewModel.agreedPrivacy = bundle.getBoolean("AGREED_PRIVACY", false)
            viewModel.agreedHealth = bundle.getBoolean("AGREED_HEALTH", false)
            viewModel.agreedLocation = bundle.getBoolean("AGREED_LOCATION", false)
        }

        // ✅ 초기 Fragment 표시
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NameFragment())
                .commit()
        }
    }

    fun navigateToNext(current: String) {
        val nextFragment = when (current) {
            "Name" -> BirthGenderFragment()
            "BirthGender" -> EmailFragment()
            "Email" -> PasswordFragment()
            "Password" -> NotificationFragment()
            "Notification" -> CompleteFragment()
            else -> null
        }

        nextFragment?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, it)
                .addToBackStack(null)
                .commit()
        }
    }
}
