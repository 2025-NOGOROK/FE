package com.example.wear.data

import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.ValueKey

object IBIDataParsing {

    fun getValidIbiList(dataPoint: DataPoint): ArrayList<Int> {
        val ibiValues = dataPoint.getValue(ValueKey.HeartRateSet.IBI_LIST) as? List<Int> ?: return arrayListOf()
        val ibiStatuses = dataPoint.getValue(ValueKey.HeartRateSet.IBI_STATUS_LIST) as? List<Int> ?: return arrayListOf()

        val validIbiList = ArrayList<Int>()
        for (i in ibiValues.indices) {
            val value = ibiValues[i]
            val status = ibiStatuses.getOrNull(i) ?: continue

            if (isIBIValid(status, value)) {
                validIbiList.add(value)
            }
        }
        return validIbiList
    }

    private fun isIBIValid(status: Int, value: Int): Boolean {
        // status 1 == valid, and value should be within reasonable physiological range
        return status == 1 && value in 300..2000
    }

    fun calculateRMSSD(ibiList: List<Long>): Double {
        if (ibiList.size < 2) return 0.0

        val diffs = ibiList.zipWithNext { a, b -> (b - a).toDouble() }
        val squaredDiffs = diffs.map { it * it }
        val mean = squaredDiffs.average()
        return kotlin.math.sqrt(mean)
    }

    fun calculateStressScore(rmssd: Double): Int {
        return when {
            rmssd >= 100 -> 0   // Low stress
            rmssd >= 50 -> 50   // Medium stress
            else -> 100         // High stress
        }
    }
}
