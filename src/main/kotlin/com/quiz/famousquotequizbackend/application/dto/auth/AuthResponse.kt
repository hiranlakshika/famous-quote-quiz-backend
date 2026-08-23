package com.quiz.famousquotequizbackend.application.dto.auth

import com.quiz.famousquotequizbackend.application.dto.user.UserResponse
import java.time.Instant

data class AuthResponse(
    val token: String,
    val expiresAt: Instant,
    val refreshToken: String,
    val refreshTokenExpiresAt: Instant,
    val user: UserResponse,
)
