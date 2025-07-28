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
import com.example.aegisai.databinding.FragmentRegisterStep1Binding
import com.example.aegisai.model.RegisterRequest
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RegisterStep1Fragment : Fragment() {
    private var _binding: FragmentRegisterStep1Binding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterStep1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sendOtpButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val phone = binding.phoneEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            viewModel.register(RegisterRequest(name, password, phone))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authState.collect { state ->
                binding.progressBar.isVisible = state is AuthState.Loading
                binding.sendOtpButton.isEnabled = state !is AuthState.Loading
                when (state) {
                    is AuthState.Success -> {
                        if (state.navigateTo == "verify_otp") {
                            Toast.makeText(context, state.message ?: "OTP Sent!", Toast.LENGTH_SHORT).show()
                            // Pass the user's name to the next steps
                            val bundle = Bundle().apply {
                                putString("userName", binding.nameEditText.text.toString().trim())
                            }
                            findNavController().navigate(R.id.action_registerStep1Fragment_to_registerStep2OtpFragment, bundle)
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