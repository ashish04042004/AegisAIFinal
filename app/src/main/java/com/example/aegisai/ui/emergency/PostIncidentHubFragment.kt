package com.example.aegisai.ui.emergency

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aegisai.ARDamageActivity
import com.example.aegisai.databinding.FragmentPostIncidentHubBinding

class PostIncidentHubFragment : Fragment() {
    private var _binding: FragmentPostIncidentHubBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPostIncidentHubBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.damageAssessmentButton.setOnClickListener {
            startActivity(Intent(requireContext(), ARDamageActivity::class.java))
        }

        binding.viewReportButton.setOnClickListener {
            // TODO: Navigate to the IncidentDetailFragment in the main_nav_graph
            Toast.makeText(context, "Viewing incident report...", Toast.LENGTH_SHORT).show()
            requireActivity().finish() // Close the emergency flow and return to the main app
        }

        binding.callInsurerButton.setOnClickListener {
            // TODO: Fetch real insurer number from SharedPreferences
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:18001234567"))
            startActivity(intent)
        }

        binding.roadsideAssistanceButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:18007654321"))
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}