package com.example.nogorok.features.auth.fcm

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging


object FCMTokenManager {

    private const val PREFS_NAME = "fcm_prefs"
    private const val KEY_FCM_TOKEN = "fcm_token"

    // 서버 또는 다른 용도로 전달할 토큰을 가져옴
    fun fetchToken(
        context: Context,
        onTokenReceived: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("FCMToken", "Token fetched: $token")
                    saveToken(context, token)
                    onTokenReceived(token)
                } else {
                    Log.e("FCMToken", "Token fetch failed", task.exception)
                    onError(task.exception ?: Exception("Unknown error"))
                }
            }
    }

    // 디바이스에 토큰 저장
    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
    }

    // 저장된 토큰 조회
    fun getSavedToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_FCM_TOKEN, null)
    }
}
