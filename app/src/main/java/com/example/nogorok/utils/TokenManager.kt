package com.example.nogorok.utils

import android.content.Context
import android.content.SharedPreferences

object TokenManager {

    private const val PREFS_NAME = "nogorok_prefs"
    private const val ACCESS_TOKEN_KEY = "access_token"
    private const val KEY_EMAIL = "email"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveAccessToken(token: String) {
        prefs.edit().putString(ACCESS_TOKEN_KEY, token).apply()
    }

    fun getAccessToken(): String? {
        return prefs.getString(ACCESS_TOKEN_KEY, null)
    }

    fun clearAccessToken() {
        prefs.edit().remove(ACCESS_TOKEN_KEY).apply()
    }

    fun saveEmail(context: Context, email: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_EMAIL, email).apply()
    }

    fun getEmail(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_EMAIL, null)
    }


}
