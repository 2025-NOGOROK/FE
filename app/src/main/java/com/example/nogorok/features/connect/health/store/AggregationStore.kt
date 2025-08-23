package com.example.nogorok.features.connect.health.store

import android.content.Context
import kotlin.math.roundToInt

object AggregationStore {
    private const val PREF = "hr_agg_pref"
    private const val KEY_SET = "keys" // SharedPreferences StringSet

    data class Bucket(
        val startMs: Long,
        var sumHr: Long = 0, var cntHr: Int = 0,
        var sumRmssd: Double = 0.0, var cntRmssd: Int = 0,
        var sumEma: Double = 0.0, var cntEma: Int = 0,
        var sumRaw: Double = 0.0, var cntRaw: Int = 0
    )

    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    private fun hourStart(ts: Long) = (ts / 3_600_000L) * 3_600_000L
    private fun keyOf(startMs: Long) = "b_$startMs"

    /** ⬅️ 누락돼서 오류났던 함수: 저장된 버킷을 읽고 없으면 새로 생성 */
    private fun readBucket(ctx: Context, startMs: Long): Bucket {
        val p = prefs(ctx)
        val key = keyOf(startMs)
        val saved = p.getString(key, null)
        return decode(saved, startMs) ?: Bucket(startMs)
    }

    fun addSample(ctx: Context, ts: Long, hr: Int?, rmssd: Double?, ema: Double?, raw: Double?) {
        val start = hourStart(ts)
        val key = keyOf(start)
        val p = prefs(ctx)

        // 객체의 필드만 수정하므로 val로 충분
        val b = readBucket(ctx, start)
        if (hr != null)    { b.sumHr += hr; b.cntHr++ }
        if (rmssd != null) { b.sumRmssd += rmssd; b.cntRmssd++ }
        if (ema != null)   { b.sumEma += ema; b.cntEma++ }
        if (raw != null)   { b.sumRaw += raw; b.cntRaw++ }

        p.edit()
            .putString(key, encode(b))
            .putStringSet(KEY_SET, (p.getStringSet(KEY_SET, emptySet())!! + key))
            .apply()
    }

    fun readCompleted(ctx: Context, now: Long = System.currentTimeMillis()): List<Bucket> {
        val p = prefs(ctx)
        val currentHour = hourStart(now)
        val out = mutableListOf<Bucket>()
        for (key in p.getStringSet(KEY_SET, emptySet())!!) {
            val start = key.removePrefix("b_").toLongOrNull() ?: continue
            if (start >= currentHour) continue
            decode(p.getString(key, null), start)?.let(out::add)
        }
        return out.sortedBy { it.startMs }
    }

    fun remove(ctx: Context, startMs: Long) {
        val p = prefs(ctx)
        val key = keyOf(startMs)
        val set = p.getStringSet(KEY_SET, emptySet())!!.toMutableSet()
        set.remove(key)
        p.edit().remove(key).putStringSet(KEY_SET, set).apply()
    }

    // ---- encoding ----
    private fun encode(b: Bucket): String =
        "${b.sumHr}|${b.cntHr}|${b.sumRmssd}|${b.cntRmssd}|${b.sumEma}|${b.cntEma}|${b.sumRaw}|${b.cntRaw}"

    private fun decode(s: String?, start: Long): Bucket? {
        if (s.isNullOrBlank()) return null
        val t = s.split("|")
        if (t.size != 8) return null
        return Bucket(
            startMs = start,
            sumHr = t[0].toLong(), cntHr = t[1].toInt(),
            sumRmssd = t[2].toDouble(), cntRmssd = t[3].toInt(),
            sumEma = t[4].toDouble(), cntEma = t[5].toInt(),
            sumRaw = t[6].toDouble(), cntRaw = t[7].toInt(),
        )
    }

    // 평균 → 서버 DTO용 변환 보조
    data class Averages(
        val tsMid: Long,
        val hr: Int,
        val rmssd: Double,
        val emaPct: Int,
        val rawPct: Int
    )

    fun toAverages(b: Bucket): Averages {
        val avgHr = if (b.cntHr > 0) (b.sumHr.toDouble() / b.cntHr).roundToInt() else 0
        val avgRmssd = if (b.cntRmssd > 0) b.sumRmssd / b.cntRmssd else 0.0
        val avgEma = if (b.cntEma > 0) b.sumEma / b.cntEma else 0.0
        val avgRaw = if (b.cntRaw > 0) b.sumRaw / b.cntRaw else 0.0
        return Averages(
            tsMid = b.startMs + 30 * 60 * 1000L,
            hr = avgHr.coerceAtLeast(0),
            rmssd = avgRmssd,
            emaPct = (avgEma * 100).roundToInt().coerceIn(0, 100),
            rawPct = (avgRaw * 100).roundToInt().coerceIn(0, 100),
        )
    }
}
