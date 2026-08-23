package com.quiz.famousquotequizbackend.application.port.driven

import com.quiz.famousquotequizbackend.domain.user.User

interface UserRepository {
    fun findByEmailIgnoreCase(email: String): User?
    fun existsByEmailIgnoreCase(email: String): Boolean
    fun findById(id: Long): User?
    fun save(user: User): User
}
