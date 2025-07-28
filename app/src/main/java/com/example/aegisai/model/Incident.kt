package com.example.aegisai.model

import java.io.Serializable

// Serializable allows us to pass this object between fragments
data class Incident(
    val id: String,
    val eventType: String, // "Crash Detected", "SOS Alert"
    val timestamp: Long,
    val location: String,
    val details: String // For the full report/briefing
) : Serializable