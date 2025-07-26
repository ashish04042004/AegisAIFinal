package com.example.aegisai

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * A simple singleton object to hold the application's monitoring status.
 * This allows the Service and Activity to communicate in a decoupled way.
 */
object MonitoringStatus {
    // This flow will emit status updates to any part of the app that is listening.
    val statusFlow = MutableStateFlow("Service is stopped.")
}