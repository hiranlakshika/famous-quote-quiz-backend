package com.quiz.famousquotequizbackend.adapter.driving.rest

import com.quiz.famousquotequizbackend.adapter.driving.rest.dto.RefreshRequest
import com.quiz.famousquotequizbackend.application.dto.auth.AuthResponse
import com.quiz.famousquotequizbackend.application.dto.auth.LoginRequest
import com.quiz.famousquotequizbackend.application.service.AuthService
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
class AuthController(private val authService: AuthService) {

    @PostMapping("/login")
    @Operation(summary = "Exchange email and password for a JWT")
    fun login(@Valid @RequestBody request: LoginRequest): AuthResponse = authService.login(request)

    @PostMapping("/refresh")
    @Operation(summary = "Exchange a refresh token for a new access token")
    fun refresh(@Valid @RequestBody request: RefreshRequest): AuthResponse =
        authService.refresh(request.refreshToken)

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Revoke a refresh token")
    fun logout(@Valid @RequestBody request: RefreshRequest) = authService.logout(request.refreshToken)
}
