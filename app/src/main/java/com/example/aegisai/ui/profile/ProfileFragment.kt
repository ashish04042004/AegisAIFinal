package com.example.aegisai.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.aegisai.R
import com.example.aegisai.databinding.FragmentProfileBinding
import com.example.aegisai.databinding.ListItemContactBinding
import com.example.aegisai.model.EmergencyContact
import com.example.aegisai.ui.auth.AuthActivity
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileNameText.text = "Name: ${viewModel.getUserName(requireContext())}"
        binding.profilePhoneText.text = "Phone: ${viewModel.getUserPhone(requireContext())}"

        // Load initial data from SharedPreferences
        viewModel.loadInitialData(requireContext())

        setupObservers()

        binding.logoutButton.setOnClickListener {
            val sharedPrefs = requireContext().getSharedPreferences("AegisAiPrefs", 0)
            sharedPrefs.edit().clear().apply()
            val intent = Intent(requireActivity(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun setupObservers() {
        // This observer automatically redraws the contacts list whenever it changes in the ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.contacts.collectLatest { contacts ->
                displayEmergencyContacts(contacts)
            }
        }

        // This observer shows a Toast for any operation status message from the ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.operationStatus.collectLatest { status ->
                status?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearOperationStatus() // Reset status after showing to prevent re-showing on config change
                }
            }
        }
    }

    private fun displayEmergencyContacts(contacts: List<EmergencyContact>) {
        binding.contactsContainer.removeAllViews() // Clear old views

        if (contacts.isNotEmpty()) {
            contacts.forEach { contact ->
                val contactBinding = ListItemContactBinding.inflate(layoutInflater)
                contactBinding.contactNameText.text = contact.name
                contactBinding.contactPhoneText.text = contact.phone

                contactBinding.editButton.setOnClickListener {
                    showAddEditContactDialog(contact)
                }
                contactBinding.deleteButton.setOnClickListener {
                    showDeleteConfirmationDialog(contact)
                }
                binding.contactsContainer.addView(contactBinding.root)
            }
        }

        // Add an "Add New" button at the end
        val addButton = MaterialButton(requireContext(), null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
            text = "Add New Contact"
            setIconResource(R.drawable.ic_add_24)
            setOnClickListener { showAddEditContactDialog(null) }
        }
        binding.contactsContainer.addView(addButton)
    }

    private fun showAddEditContactDialog(contact: EmergencyContact?) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_contact, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.name_edit_text)
        val phoneEditText = dialogView.findViewById<EditText>(R.id.phone_edit_text)

        val isEditing = contact != null
        if (isEditing) {
            nameEditText.setText(contact?.name)
            phoneEditText.setText(contact?.phone)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(if (isEditing) "Edit Contact" else "Add Contact")
            .setView(dialogView)
            .setPositiveButton(if (isEditing) "Save" else "Add") { _, _ ->
                val name = nameEditText.text.toString().trim()
                val phone = phoneEditText.text.toString().trim()
                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    if (isEditing) {
                        viewModel.updateContact(requireContext(), contact!!.phone, name, phone)
                    } else {
                        viewModel.addContact(requireContext(), name, phone)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmationDialog(contact: EmergencyContact) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Contact")
            .setMessage("Are you sure you want to delete ${contact.name}?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteContact(requireContext(), contact)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}