package com.quiz.famousquotequizbackend.auth

import java.time.Instant

data class IssuedToken(
    val value: String,
    val expiresAt: Instant,
)
