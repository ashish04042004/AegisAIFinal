package com.example.aegisai.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.aegisai.CrashDetectionService
import com.example.aegisai.MonitoringStatus
import com.example.aegisai.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Handler for the SOS button long-press
    private val handler = Handler(Looper.getMainLooper())
    private var isSosPressed = false

    // Modern way to handle permission requests in a Fragment
    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Check if all necessary permissions were granted
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                startMonitoringService()
            } else {
                Toast.makeText(requireContext(), "Permissions are required for monitoring functionality.", Toast.LENGTH_LONG).show()
            }
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // 1. Fetch and display the user's name from SharedPreferences
        val sharedPrefs = requireContext().getSharedPreferences("AegisAiPrefs", Context.MODE_PRIVATE)
        val userName = sharedPrefs.getString("USER_NAME", "User") // Use "User" as a default fallback

        // Capitalize the first letter for a more polished UI
        val capitalizedUserName = userName?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
        binding.welcomeText.text = "Good morning, $capitalizedUserName"

        // 2. Automatically start monitoring when the user lands on the dashboard
        checkPermissionsAndStartService()

        // 3. Set up listeners for all interactive UI elements
        setupListeners()

        // 4. Observe the monitoring status to update the UI in real-time
        viewLifecycleOwner.lifecycleScope.launch {
            MonitoringStatus.statusFlow.collect { status ->
                binding.statusSubtitle.text = status
            }
        }

        return binding.root
    }

    private fun setupListeners() {
        // SOS Button Long Press Logic
        binding.sosCard.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isSosPressed = true
                    // Start a 3-second timer
                    handler.postDelayed({
                        if (isSosPressed) triggerSosAlert()
                    }, 3000) // 3000 milliseconds = 3 seconds
                    true // We handled the event
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isSosPressed = false
                    // Cancel the timer if the user releases before 3 seconds
                    handler.removeCallbacksAndMessages(null)
                    true // We handled the event
                }
                else -> false
            }
        }

        // Click listeners for the quick action cards
        binding.contactsCard.setOnClickListener { showToast("Emergency Contacts clicked") }
        binding.testSystemCard.setOnClickListener { showToast("Test System clicked") }
        binding.myVehicleCard.setOnClickListener { showToast("My Vehicle clicked") }
    }

    private fun checkPermissionsAndStartService() {
        val permissionsToRequest = mutableListOf<String>()

        // Check for location permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // Check for notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            // Request the missing permissions
            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            // All permissions are already granted, start the service
            startMonitoringService()
        }
    }

    private fun startMonitoringService() {
        val intent = Intent(requireContext(), CrashDetectionService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(intent)
        } else {
            requireActivity().startService(intent)
        }
    }

    private fun triggerSosAlert() {
        // TODO: Implement the actual SOS logic.
        // This could involve calling a ViewModel to send an alert via an API,
        // sending SMS messages, etc.
        showToast("SOS Alert Triggered!")
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up the handler and binding to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}