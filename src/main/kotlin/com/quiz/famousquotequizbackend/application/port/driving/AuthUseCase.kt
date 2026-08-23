package com.quiz.famousquotequizbackend.application.port.driving

import com.quiz.famousquotequizbackend.application.dto.auth.AuthResponse

interface AuthUseCase {
    fun login(email: String, password: String): AuthResponse
    fun refresh(refreshToken: String): AuthResponse
    fun logout(refreshToken: String)
}
