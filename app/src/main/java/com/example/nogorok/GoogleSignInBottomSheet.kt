package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GoogleSignInBottomSheet : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // 모달 레이아웃을 inflate
        return inflater.inflate(R.layout.dialog_google_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 2초(2000ms) 후에 연동중 페이지로 자동 이동
        Handler(Looper.getMainLooper()).postDelayed({
            // 모달을 닫고
            dismiss()
            // 연동중(로딩) 페이지로 이동
            activity?.let {
                val intent = Intent(it, CalendarSyncingActivity::class.java)
                it.startActivity(intent)
            }
        }, 2000)
    }
}