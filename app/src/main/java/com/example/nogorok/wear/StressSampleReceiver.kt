package com.example.nogorok.wear

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.nogorok.features.connect.health.store.AggregationStore

class StressSampleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_STRESS_UPDATE) return

        val ema   = intent.getDoubleExtra(EXTRA_STRESS_EMA, Double.NaN)
        val raw   = intent.getDoubleExtra(EXTRA_STRESS_RAW, Double.NaN)
        val hr    = intent.getIntExtra(EXTRA_HR, -1)
        val rmssd = intent.getDoubleExtra(EXTRA_RMSSD, Double.NaN)
        val ts    = intent.getLongExtra(EXTRA_TS, 0L)
        val tsActual = if (ts != 0L) ts else System.currentTimeMillis()

        Log.d("StressSampleReceiver", "addSample ts=$tsActual hr=$hr ema=$ema raw=$raw rmssd=$rmssd")

        AggregationStore.addSample(
            ctx = context.applicationContext,   // ← 이름을 ctx 로 맞춤
            ts = tsActual,
            hr = hr.takeIf { it >= 0 },
            rmssd = rmssd.takeIf { !it.isNaN() },
            ema = ema.takeIf { !it.isNaN() },
            raw = raw.takeIf { !it.isNaN() }
        )
        // 또는 위치 기반 파라미터로도 가능:
        // AggregationStore.addSample(context.applicationContext, tsActual, hr.takeIf { it >= 0 }, rmssd.takeIf { !it.isNaN() }, ema.takeIf { !it.isNaN() }, raw.takeIf { !it.isNaN() })
    }
}
