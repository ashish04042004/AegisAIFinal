package com.example.aegisai.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.aegisai.MainActivity
import com.example.aegisai.databinding.FragmentRegisterStep3DetailsBinding
import com.example.aegisai.model.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RegisterStep3DetailsFragment : Fragment() {
    private var _binding: FragmentRegisterStep3DetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()
    private var userNameFromStep1 = "" // To hold the user's name

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // This is a simple way to get the name from step 1.
        // A better long-term solution might involve passing it via NavArgs.
        userNameFromStep1 = arguments?.getString("userName") ?: ""

        binding.completeButton.setOnClickListener {
            val request = CompleteRegistrationRequest(
                phone = viewModel.phoneForRegistration,
                emergencyContact = listOf(
                    EmergencyContact(binding.contact1NameEditText.text.toString(), binding.contact1PhoneEditText.text.toString()),
                    EmergencyContact(binding.contact2NameEditText.text.toString(), binding.contact2PhoneEditText.text.toString())
                ),
                vehicleDetails = VehicleDetails(
                    type = "Car",
                    make = binding.makeEditText.text.toString(),
                    model = binding.modelEditText.text.toString(),
                    year = binding.yearEditText.text.toString(),
                    licensePlate = binding.licenseEditText.text.toString()
                ),
                insuranceDetails = InsuranceDetails(
                    provider = binding.providerEditText.text.toString(),
                    policyNo = binding.policyNoEditText.text.toString()
                )
            )
            // Pass context and name to ViewModel
            viewModel.completeRegistration(requireContext(), request, userNameFromStep1)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authState.collect { state ->
                binding.progressBar.isVisible = state is AuthState.Loading
                binding.completeButton.isEnabled = state !is AuthState.Loading
                when (state) {
                    is AuthState.Success -> {
                        if (state.navigateTo == "main_app") {
                            Toast.makeText(context, "Registration Complete!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(activity, MainActivity::class.java))
                            activity?.finish()
                        }
                        viewModel.resetState()
                    }
                    is AuthState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        viewModel.resetState()
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterStep3DetailsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}