package com.example.aegisai.network

import com.example.aegisai.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<GenericResponse>

    @POST("verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<GenericResponse>

    @POST("complete-registration")
    suspend fun completeRegistration(@Body request: CompleteRegistrationRequest): Response<GenericResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<GenericResponse>
}