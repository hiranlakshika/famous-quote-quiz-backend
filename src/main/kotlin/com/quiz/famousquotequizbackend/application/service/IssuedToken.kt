package com.quiz.famousquotequizbackend.application.service

import java.time.Instant

data class IssuedToken(
    val value: String,
    val expiresAt: Instant,
)
