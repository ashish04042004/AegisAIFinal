package com.example.aegisai.model

data class RegisterRequest(val name: String, val password: String, val phone: String)
data class VerifyOtpRequest(val phone: String, val otp: String)
data class LoginRequest(val phone: String, val password: String)

data class CompleteRegistrationRequest(
    val phone: String,
    val emergencyContact: List<EmergencyContact>,
    val vehicleDetails: VehicleDetails,
    val insuranceDetails: InsuranceDetails
)
data class IncidentReportRequest(
    val phone: String, // Identifies the user
    val gps: String,
    val timestamp: String,
    val severity: Int,
    val analysis: String
)

data class EmergencyContact(val name: String, val phone: String)
data class VehicleDetails(val type: String, val make: String, val model: String, val year: String, val licensePlate: String)
data class InsuranceDetails(val provider: String, val policyNo: String)

data class GenericResponse(val success: Boolean, val message: String)