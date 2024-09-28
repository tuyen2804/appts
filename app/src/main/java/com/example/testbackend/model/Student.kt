package com.example.testbackend.model

data class Student(
    val accountId: String,
    val urlImageStudent: String,
    val urlFrontCard: String,
    val urlBackCard: String,
    val fullName: String,
    val gender: String,
    val birthDate: String,
    val city: String,
    val district: String,
    val ward: String,
    val idNumber: String,
    val issueDate: String,
    val nationality: String,
    val ethnicity: String,
    val religion: String
)