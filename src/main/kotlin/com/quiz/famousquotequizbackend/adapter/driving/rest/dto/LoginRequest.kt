package com.quiz.famousquotequizbackend.adapter.driving.rest.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Enter a valid email")
    val email: String,

    @field:NotBlank(message = "Password is required")
    val password: String,
)
