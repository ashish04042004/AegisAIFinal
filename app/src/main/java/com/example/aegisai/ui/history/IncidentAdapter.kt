package com.example.aegisai.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aegisai.R
import com.example.aegisai.databinding.ListItemIncidentBinding
import com.example.aegisai.model.Incident
import java.text.SimpleDateFormat
import java.util.*

class IncidentAdapter(
    private val incidents: List<Incident>,
    private val onClick: (Incident) -> Unit
) : RecyclerView.Adapter<IncidentAdapter.IncidentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncidentViewHolder {
        val binding = ListItemIncidentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IncidentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IncidentViewHolder, position: Int) {
        val incident = incidents[position]
        holder.bind(incident)
        holder.itemView.setOnClickListener { onClick(incident) }
    }

    override fun getItemCount(): Int = incidents.size

    class IncidentViewHolder(private val binding: ListItemIncidentBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("MMMM dd, yyyy, h:mm a", Locale.getDefault())

        fun bind(incident: Incident) {
            binding.textEventType.text = incident.eventType
            binding.textTimestamp.text = dateFormat.format(Date(incident.timestamp))
            binding.textLocation.text = incident.location

            // Set icon based on event type
            val iconRes = when {
                incident.eventType.contains("Crash", ignoreCase = true) -> R.drawable.ic_vehicle_24
                incident.eventType.contains("SOS", ignoreCase = true) -> R.drawable.ic_help_24
                else -> R.drawable.ic_test_system_24
            }
            binding.iconEventType.setImageResource(iconRes)
        }
    }
}