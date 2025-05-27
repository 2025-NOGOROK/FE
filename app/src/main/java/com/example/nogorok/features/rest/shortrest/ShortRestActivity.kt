package com.example.nogorok.features.rest.shortrest

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.fragment.app.DialogFragment
import com.example.nogorok.R

// 오버레이처럼 동작하는 DialogFragment야!
class ShortRestActivity(
    private val onComplete: () -> Unit // 2초 후 실행될 콜백
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.activity_short_rest)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false) // 백버튼으로 닫히지 않게

        // 2초 후 콜백 실행하고 다이얼로그 종료
        Handler(Looper.getMainLooper()).postDelayed({
            onComplete()
            dismiss()
        }, 2000)

        return dialog
    }
}
