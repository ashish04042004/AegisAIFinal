package com.example.aegisai.ui.emergency

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.aegisai.CrashDetectionService
import com.example.aegisai.R
import com.example.aegisai.databinding.FragmentCountdownBinding

class CountdownFragment : Fragment() {
    private var _binding: FragmentCountdownBinding? = null
    private val binding get() = _binding!!
    private var timer: CountDownTimer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCountdownBinding.inflate(inflater, container, false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { /* Do nothing to prevent accidental cancellation */ }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelButton.setOnClickListener {
            timer?.cancel()
            // Stop the monitoring service since this was a false alarm
            requireActivity().stopService(Intent(requireContext(), CrashDetectionService::class.java))
            requireActivity().finish() // Close the emergency screen
        }

        startCountdown()
    }

    private fun startCountdown() {
        timer = object : CountDownTimer(10000, 1000) { // 10 seconds
            override fun onTick(millisUntilFinished: Long) {
                binding.countdownText.text = (millisUntilFinished / 1000 + 1).toString()
            }

            override fun onFinish() {
                // TODO: Trigger the actual API call to send alerts to contacts/services
                if (isAdded) { // Ensure the fragment is still attached to the activity
                    findNavController().navigate(R.id.action_countdownFragment_to_alertsSentFragment)
                }
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        _binding = null
    }
}