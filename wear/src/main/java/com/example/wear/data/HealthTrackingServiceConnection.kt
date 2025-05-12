package com.example.wear.data

import android.content.Context
import android.util.Log
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "HealthTrackingServiceConnection"

@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class HealthTrackingServiceConnection @Inject constructor(
    @ApplicationContext private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    private var connected: Boolean = false
    private var healthTrackingService: HealthTrackingService? = null

    val connectionFlow = callbackFlow {
        val connectionListener = object : ConnectionListener {
            override fun onConnectionSuccess() {
                connected = true
                Log.i(TAG, "onConnectionSuccess()")
                coroutineScope.runCatching {
                    trySendBlocking(ConnectionMessage.ConnectionSuccessMessage)
                }
            }

            override fun onConnectionFailed(connectionException: HealthTrackerException?) {
                connected = false
                Log.e(TAG, "onConnectionFailed: ${connectionException?.message}")
                coroutineScope.runCatching {
                    trySendBlocking(ConnectionMessage.ConnectionFailedMessage(connectionException))
                }
            }

            override fun onConnectionEnded() {
                connected = false
                Log.i(TAG, "onConnectionEnded()")
                coroutineScope.runCatching {
                    trySendBlocking(ConnectionMessage.ConnectionEndedMessage)
                }
                close()
            }
        }

        Log.i(TAG, "Connecting HealthTrackingService")
        healthTrackingService = HealthTrackingService(connectionListener, context)
        healthTrackingService!!.connectService()

        awaitClose {
            Log.i(TAG, "Disconnecting HealthTrackingService")
            healthTrackingService?.disconnectService()
            connected = false
        }
    }

    fun getHealthTrackingService(): HealthTrackingService? {
        return healthTrackingService
    }
}

sealed class ConnectionMessage {
    object ConnectionSuccessMessage : ConnectionMessage()
    class ConnectionFailedMessage(val exception: HealthTrackerException?) : ConnectionMessage()
    object ConnectionEndedMessage : ConnectionMessage()
}
