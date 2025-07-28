package com.example.aegisai.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.aegisai.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.incidents.observe(viewLifecycleOwner) { incidents ->
            if (incidents.isNullOrEmpty()) {
                binding.emptyStateText.isVisible = true
                binding.historyRecyclerView.isVisible = false
            } else {
                binding.emptyStateText.isVisible = false
                binding.historyRecyclerView.isVisible = true
                binding.historyRecyclerView.adapter = IncidentAdapter(incidents) { incident ->
                    // Handle item click, e.g., navigate to a detail screen
                    Toast.makeText(context, "Clicked on incident: ${incident.id}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}