package com.example.aegisai.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aegisai.model.CompleteRegistrationRequest
import com.example.aegisai.model.LoginRequest
import com.example.aegisai.model.RegisterRequest
import com.example.aegisai.model.VerifyOtpRequest
import com.example.aegisai.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val navigateTo: String, val message: String? = null) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    var phoneForRegistration: String = ""
        private set

    private val api = RetrofitClient.instance

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = api.register(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    phoneForRegistration = request.phone
                    _authState.value = AuthState.Success("verify_otp", response.body()?.message)
                } else {
                    _authState.value = AuthState.Error(response.body()?.message ?: "Registration failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Network error: ${e.message}")
            }
        }
    }

    fun verifyOtp(otp: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val request = VerifyOtpRequest(phone = phoneForRegistration, otp = otp)
            try {
                val response = api.verifyOtp(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    _authState.value = AuthState.Success("complete_registration")
                } else {
                    _authState.value = AuthState.Error(response.body()?.message ?: "OTP verification failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Network error: ${e.message}")
            }
        }
    }

    fun completeRegistration(request: CompleteRegistrationRequest) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = api.completeRegistration(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    _authState.value = AuthState.Success("main_app")
                } else {
                    _authState.value = AuthState.Error(response.body()?.message ?: "Final registration failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Network error: ${e.message}")
            }
        }
    }

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = api.login(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    _authState.value = AuthState.Success("main_app")
                } else {
                    _authState.value = AuthState.Error(response.body()?.message ?: "Login failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Network error: ${e.message}")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}