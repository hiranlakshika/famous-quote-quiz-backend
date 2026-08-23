package com.quiz.famousquotequizbackend.infrastructure.config

import com.quiz.famousquotequizbackend.application.port.driven.AuthSettings
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("auth")
data class AuthProperties(
    /** HMAC key used to sign the JWTs, at least 32 characters long. */
    val secret: String,
    val tokenTtl: Duration = Duration.ofMinutes(15),
    override val refreshTokenTtl: Duration = Duration.ofDays(30),
) : AuthSettings
