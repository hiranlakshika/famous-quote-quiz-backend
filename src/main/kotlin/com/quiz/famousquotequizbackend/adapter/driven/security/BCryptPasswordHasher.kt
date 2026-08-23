package com.quiz.famousquotequizbackend.adapter.driven.security

import com.quiz.famousquotequizbackend.application.port.driven.PasswordHasher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class BCryptPasswordHasher(private val encoder: PasswordEncoder) : PasswordHasher {

    override fun encode(raw: String): String = checkNotNull(encoder.encode(raw))

    override fun matches(raw: String, passwordHash: String): Boolean = encoder.matches(raw, passwordHash)
}
