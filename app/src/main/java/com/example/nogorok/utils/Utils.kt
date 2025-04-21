package com.example.nogorok.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.samsung.android.sdk.health.data.error.ResolvablePlatformException
import kotlinx.coroutines.CoroutineExceptionHandler
import java.util.Locale

// 간단한 Toast 메시지 함수
fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

// 공통 예외 핸들러: Samsung Health SDK 예외 처리 포함
fun getExceptionHandler(
    activity: Activity,
    exceptionResponse: MutableLiveData<String>
): CoroutineExceptionHandler {
    return CoroutineExceptionHandler { _, exception ->
        if ((exception is ResolvablePlatformException) && exception.hasResolution) {
            exception.resolve(activity)
        }
        exceptionResponse.postValue(exception.message ?: "Unknown error")
    }
}

// 💡 평균 심박수 포맷 함수 추가
fun formatString(input: Float): String {
    return String.format(Locale.ENGLISH, "%.2f", input)
}
