package com.quiz.famousquotequizbackend.application.service

import com.quiz.famousquotequizbackend.application.dto.auth.AuthResponse
import com.quiz.famousquotequizbackend.application.dto.user.UserResponse
import com.quiz.famousquotequizbackend.application.exception.UnauthorizedException
import com.quiz.famousquotequizbackend.application.port.driven.AuthSettings
import com.quiz.famousquotequizbackend.application.port.driven.PasswordHasher
import com.quiz.famousquotequizbackend.application.port.driven.RefreshTokenRepository
import com.quiz.famousquotequizbackend.application.port.driven.TokenIssuer
import com.quiz.famousquotequizbackend.application.port.driven.UserRepository
import com.quiz.famousquotequizbackend.application.port.driving.AuthUseCase
import com.quiz.famousquotequizbackend.domain.auth.RefreshToken
import com.quiz.famousquotequizbackend.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.time.Instant
import java.util.Base64

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordHasher: PasswordHasher,
    private val tokenIssuer: TokenIssuer,
    private val authSettings: AuthSettings,
) : AuthUseCase {

    private val random = SecureRandom()

    override fun login(email: String, password: String): AuthResponse {
        val user = userRepository.findByEmailIgnoreCase(email.trim())
            ?: throw UnauthorizedException("Invalid email or password")
        if (!passwordHasher.matches(password, user.passwordHash)) {
            throw UnauthorizedException("Invalid email or password")
        }
        return authResponseFor(user)
    }

    /** Exchanges a refresh token for a new access token, rotating the refresh token in the process. */
    override fun refresh(refreshToken: String): AuthResponse {
        val stored = refreshTokenRepository.findByValue(refreshToken)
            ?: throw UnauthorizedException("Invalid refresh token")
        refreshTokenRepository.delete(stored)
        if (stored.expiresAt.isBefore(Instant.now())) {
            throw UnauthorizedException("Refresh token has expired")
        }
        return authResponseFor(stored.user)
    }

    override fun logout(refreshToken: String) {
        refreshTokenRepository.deleteByValue(refreshToken)
    }

    private fun authResponseFor(user: User): AuthResponse {
        val accessToken = tokenIssuer.issue(user)
        val refreshToken = refreshTokenRepository.save(
            RefreshToken(
                value = newRefreshTokenValue(),
                user = user,
                expiresAt = Instant.now().plus(authSettings.refreshTokenTtl),
            )
        )
        return AuthResponse(
            token = accessToken.value,
            expiresAt = accessToken.expiresAt,
            refreshToken = refreshToken.value,
            refreshTokenExpiresAt = refreshToken.expiresAt,
            user = UserResponse.from(user),
        )
    }

    private fun newRefreshTokenValue(): String {
        val bytes = ByteArray(32).also(random::nextBytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}
