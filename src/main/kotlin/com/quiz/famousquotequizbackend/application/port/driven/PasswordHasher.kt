package com.quiz.famousquotequizbackend.application.port.driven

interface PasswordHasher {
    fun encode(raw: String): String
    fun matches(raw: String, passwordHash: String): Boolean
}
