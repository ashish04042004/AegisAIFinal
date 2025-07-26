package com.example.aegisai.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.aegisai.R
import com.example.aegisai.databinding.FragmentRegisterStep2OtpBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RegisterStep2OtpFragment : Fragment() {
    private var _binding: FragmentRegisterStep2OtpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterStep2OtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.otpPromptText.text = "Enter the OTP sent to ${viewModel.phoneForRegistration}"

        binding.verifyButton.setOnClickListener {
            val otp = binding.otpEditText.text.toString().trim()
            viewModel.verifyOtp(otp)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authState.collect { state ->
                binding.progressBar.isVisible = state is AuthState.Loading
                binding.verifyButton.isEnabled = state !is AuthState.Loading
                when (state) {
                    is AuthState.Success -> {
                        if (state.navigateTo == "complete_registration") {
                            Toast.makeText(context, "Phone Verified!", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_registerStep2OtpFragment_to_registerStep3DetailsFragment)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}