package com.example.nogorok.network.dto

data class SignUpRequest(
    val name: String,
    val birth: String,
    val gender: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val pushNotificationAgreed: Boolean,
    val deviceToken: String,
    val termsOfServiceAgreed: Boolean,
    val privacyPolicyAgreed: Boolean,
    val healthInfoPolicyAgreed: Boolean,
    val locationPolicyAgreed: Boolean
)
