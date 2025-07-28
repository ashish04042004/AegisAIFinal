package com.example.aegisai.network

import com.example.aegisai.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<GenericResponse>

    @POST("verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<GenericResponse>

    @POST("complete-registration")
    suspend fun completeRegistration(@Body request: CompleteRegistrationRequest): Response<GenericResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    @POST("emergency-contact/add")
    suspend fun addContact(@Body request: AddContactRequest): Response<GenericResponse>

    @HTTP(method = "DELETE", path = "emergency-contact/delete", hasBody = true)
    suspend fun deleteContact(@Body request: DeleteContactRequest): Response<GenericResponse>

    @PUT("emergency-contact/update") // <-- CHANGED from @POST
    suspend fun updateContact(@Body request: UpdateContactRequest): Response<GenericResponse>// Updated return type
}