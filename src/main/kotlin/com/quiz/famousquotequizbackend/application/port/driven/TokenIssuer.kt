package com.quiz.famousquotequizbackend.application.port.driven

import com.quiz.famousquotequizbackend.domain.user.User
import java.time.Instant

data class IssuedToken(
    val value: String,
    val expiresAt: Instant,
)

interface TokenIssuer {
    fun issue(user: User): IssuedToken
}
