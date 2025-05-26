package com.example.nogorok.utils

import android.content.Context
import android.content.SharedPreferences

object TokenManager {

    private const val PREFS_NAME = "nogorok_prefs"
    private const val ACCESS_TOKEN_KEY = "access_token"
    private const val KEY_EMAIL = "email"

    // ✅ 항상 applicationContext 사용
    private fun getPrefs(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveAccessToken(context: Context, token: String) {
        getPrefs(context).edit().putString(ACCESS_TOKEN_KEY, token).apply()
    }

    fun getAccessToken(context: Context): String? {
        return getPrefs(context).getString(ACCESS_TOKEN_KEY, null)
    }

    fun clearAccessToken(context: Context) {
        getPrefs(context).edit().remove(ACCESS_TOKEN_KEY).apply()
    }

    fun saveEmail(context: Context, email: String) {
        getPrefs(context).edit().putString(KEY_EMAIL, email).apply()
    }

    fun getEmail(context: Context): String? {
        return getPrefs(context).getString(KEY_EMAIL, null)
    }
}
