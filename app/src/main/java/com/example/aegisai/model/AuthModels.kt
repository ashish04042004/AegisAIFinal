package com.example.aegisai.model

// --- Add these new classes ---

data class LoginResponse(
    val success: Boolean,
    val user: User?, // The user object can be null on failure
    val message: String,
    val token: String?
)

data class User(
    val _id: String,
    val name: String,
    val phone: String,
    val emergencyContact: List<EmergencyContact>?,
    val vehicleDetails: VehicleDetails?,
    val insuranceDetails: InsuranceDetails?
)


// --- These are your existing classes (no changes needed) ---

data class RegisterRequest(val name: String, val password: String, val phone: String)
data class VerifyOtpRequest(val phone: String, val otp: String)
data class LoginRequest(val phone: String, val password: String)

data class CompleteRegistrationRequest(
    val phone: String,
    val emergencyContact: List<EmergencyContact>,
    val vehicleDetails: VehicleDetails,
    val insuranceDetails: InsuranceDetails
)

data class EmergencyContact(val name: String, val phone: String)
data class VehicleDetails(val type: String, val make: String, val model: String, val year: String, val licensePlate: String)
data class InsuranceDetails(val provider: String, val policyNo: String)

data class GenericResponse(val success: Boolean, val message: String)
data class AddContactRequest(
    val userId: String,
    val name: String,
    val phone: String
)

data class DeleteContactRequest(
    val userId: String,
    val phone: String
)

data class UpdateContactRequest(
    val userId: String,
    val oldPhone: String,
    val name: String? = null, // Optional
    val phone: String? = null  // Optional
)