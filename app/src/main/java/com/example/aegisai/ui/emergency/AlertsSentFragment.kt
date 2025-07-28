package com.example.aegisai.ui.emergency

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.aegisai.R
import com.example.aegisai.databinding.FragmentAlertsSentBinding

class AlertsSentFragment : Fragment() {
    private var _binding: FragmentAlertsSentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAlertsSentBinding.inflate(inflater, container, false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { /* Do nothing */ }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iamOkButton.setOnClickListener {
            // TODO: Trigger API call to update status to "User is conscious"
            Toast.makeText(context, "Status updated. Help is still on the way.", Toast.LENGTH_LONG).show()
        }

        // Automatically navigate to the hub after a delay
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) {
                findNavController().navigate(R.id.action_alertsSentFragment_to_postIncidentHubFragment)
            }
        }, 8000) // 8-second delay
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}