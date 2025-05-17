package com.example.nogorok.features.auth.register

import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {
    var name: String? = null
    var birth: String? = null
    var gender: String? = null
    var email: String? = null
    var password: String? = null
    var confirmPassword: String? = null
    var notificationAgreed: Boolean = false
    var deviceToken: String? = null
    var agreedService = false
    var agreedPrivacy = false
    var agreedHealth = false
    var agreedLocation = false
}
