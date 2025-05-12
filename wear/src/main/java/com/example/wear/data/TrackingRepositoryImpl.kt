package com.example.wear.data

import android.util.Log
import com.example.common.TrackedData
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

private const val TAG = "TrackingRepositoryImpl"

class TrackingRepositoryImpl @Inject constructor(
    private val healthTrackingServiceConnection: HealthTrackingServiceConnection
) : TrackingRepository {

    private var heartRateTracker: HealthTracker? = null

    override fun startTracking(): Flow<TrackedData> = callbackFlow {
        val healthTrackingService: HealthTrackingService =
            healthTrackingServiceConnection.getHealthTrackingService()
                ?: run {
                    close()
                    return@callbackFlow
                }

        heartRateTracker = healthTrackingService.getHealthTracker(HealthTrackerType.HEART_RATE)

        val listener = object : HealthTracker.TrackerEventListener {
            override fun onDataReceived(dataPoints: MutableList<DataPoint>) {
                for (dataPoint in dataPoints) {
                    val hr = dataPoint.getValue(ValueKey.HeartRateSet.HEART_RATE) as? Int ?: 0
                    val ibiList = IBIDataParsing.getValidIbiList(dataPoint)

                    val trackedData = TrackedData(hr = hr, ibi = ibiList)
                    trySendBlocking(trackedData)
                }
            }

            override fun onFlushCompleted() {
                Log.d(TAG, "Flush completed")
            }

            override fun onError(error: HealthTracker.TrackerError?) {
                Log.e(TAG, "Tracker error: $error")
            }
        }

        heartRateTracker?.setEventListener(listener)

        awaitClose {
            heartRateTracker?.unsetEventListener()
        }
    }

    override fun stopTracking() {
        heartRateTracker?.unsetEventListener()
        heartRateTracker = null
    }
}
