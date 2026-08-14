package com.quiz.famousquotequizbackend.auth

import com.quiz.famousquotequizbackend.common.UnauthorizedException
import com.quiz.famousquotequizbackend.user.User
import com.quiz.famousquotequizbackend.user.UserRepository
import com.quiz.famousquotequizbackend.user.UserResponse
import org.springframework.security.crypto.password.PasswordEncoder
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
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authProperties: AuthProperties,
) {

    private val random = SecureRandom()

    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmailIgnoreCase(request.email.trim())
            ?: throw UnauthorizedException("Invalid email or password")
        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw UnauthorizedException("Invalid email or password")
        }
        return authResponseFor(user)
    }

    /** Exchanges a refresh token for a new access token, rotating the refresh token in the process. */
    fun refresh(refreshToken: String): AuthResponse {
        val stored = refreshTokenRepository.findByValue(refreshToken)
            ?: throw UnauthorizedException("Invalid refresh token")
        refreshTokenRepository.delete(stored)
        if (stored.expiresAt.isBefore(Instant.now())) {
            throw UnauthorizedException("Refresh token has expired")
        }
        return authResponseFor(stored.user)
    }

    fun logout(refreshToken: String) {
        refreshTokenRepository.deleteByValue(refreshToken)
    }

    @Transactional(readOnly = true)
    fun userOf(subject: String?): User = subject?.toLongOrNull()
        ?.let { userRepository.findById(it).orElse(null) }
        ?: throw UnauthorizedException("Invalid or expired token")

    private fun authResponseFor(user: User): AuthResponse {
        val accessToken = jwtService.issue(user)
        val refreshToken = refreshTokenRepository.save(
            RefreshToken(
                value = newRefreshTokenValue(),
                user = user,
                expiresAt = Instant.now().plus(authProperties.refreshTokenTtl),
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
