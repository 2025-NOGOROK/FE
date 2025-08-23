package com.example.nogorok.network.util

import com.example.nogorok.network.dto.ApiError
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response

fun ResponseBody?.asApiError(): ApiError? = try {
    this?.string()?.let { raw ->
        JSONObject(raw).let {
            ApiError(
                error = it.optString("error", null),
                message = it.optString("message", null),
                authUrl = it.optString("authUrl", null)
            )
        }
    }
} catch (_: Throwable) { null }

fun <T> Response<T>.apiError(): ApiError? = this.errorBody().asApiError()
