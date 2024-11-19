package com.example.testbackend.model

data class Student(
    val accountId: String,
    val photoUrl: String,
    val idCardFrontUrl: String,
    val idCardBackUrl: String,
    val fullName: String,
    val gender: String,
    val birthDate: String,
    val birthCity: String,
    val birthDistrict: String,
    val birthWard: String,
    val idNumber: String,
    val issueDate: String,
    val issuePlace: String,
    val nationality: String,
    val ethnicity: String,
    val religion: String
)