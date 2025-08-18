package com.example.nogorok.wear

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AutoStartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 부팅 직후 / 앱 업데이트 직후 항상 서비스 재기동
        StressReceiverService.start(context)
    }
}