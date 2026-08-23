package com.quiz.famousquotequizbackend.adapter.driving.rest

import com.quiz.famousquotequizbackend.adapter.driving.rest.dto.LoginRequest
import com.quiz.famousquotequizbackend.adapter.driving.rest.dto.RefreshRequest
import com.quiz.famousquotequizbackend.application.dto.auth.AuthResponse
import com.quiz.famousquotequizbackend.application.port.driving.AuthUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
class AuthController(private val authUseCase: AuthUseCase) {

    @PostMapping("/login")
    @Operation(summary = "Exchange email and password for a JWT")
    fun login(@Valid @RequestBody request: LoginRequest): AuthResponse =
        authUseCase.login(request.email, request.password)

    @PostMapping("/refresh")
    @Operation(summary = "Exchange a refresh token for a new access token")
    fun refresh(@Valid @RequestBody request: RefreshRequest): AuthResponse =
        authUseCase.refresh(request.refreshToken)

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Revoke a refresh token")
    fun logout(@Valid @RequestBody request: RefreshRequest) = authUseCase.logout(request.refreshToken)
}
