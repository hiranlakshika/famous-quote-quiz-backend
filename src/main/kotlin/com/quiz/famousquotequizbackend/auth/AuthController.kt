package com.quiz.famousquotequizbackend.auth

import com.quiz.famousquotequizbackend.user.UserResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

data class LoginRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Enter a valid email")
    val email: String,

    @field:NotBlank(message = "Password is required")
    val password: String,
)

data class RefreshRequest(
    @field:NotBlank(message = "Refresh token is required")
    val refreshToken: String,
)

data class AuthResponse(
    val token: String,
    val expiresAt: Instant,
    val refreshToken: String,
    val refreshTokenExpiresAt: Instant,
    val user: UserResponse,
)

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
