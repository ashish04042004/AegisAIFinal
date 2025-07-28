package com.example.aegisai.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aegisai.model.Incident

class HistoryViewModel : ViewModel() {

    private val _incidents = MutableLiveData<List<Incident>>()
    val incidents: LiveData<List<Incident>> = _incidents

    init {
        loadDummyIncidents()
    }

    private fun loadDummyIncidents() {
        // In a real app, you would fetch this from a local Room database or a remote API.
        val dummyList = listOf(
            Incident(
                id = "INC001",
                eventType = "Crash Detected",
                timestamp = System.currentTimeMillis() - 86400000, // 1 day ago
                location = "Smriti Nagar, Bhilai",
                details = "Severe front-end impact detected. Airbags deployed. Briefing sent to emergency contacts."
            ),
            Incident(
                id = "INC002",
                eventType = "SOS Alert Triggered",
                timestamp = System.currentTimeMillis() - 172800000, // 2 days ago
                location = "Jawahar Nagar, Jaipur",
                details = "Manual SOS alert triggered by user. Location sent to emergency contacts."
            ),
            Incident(
                id = "INC003",
                eventType = "System Test",
                timestamp = System.currentTimeMillis() - 259200000, // 3 days ago
                location = "N/A",
                details = "User-initiated system test. All systems nominal."
            )
        )
        _incidents.value = dummyList
    }
}