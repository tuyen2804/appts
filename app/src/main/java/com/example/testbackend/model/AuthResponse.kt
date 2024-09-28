package com.example.testbackend.model

data class AuthResponse(
    val auth: Boolean,
    val accessToken: String?,
    val refreshToken: String?,
    val role:String?,
    val account_id:String?
)

