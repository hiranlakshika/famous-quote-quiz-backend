package com.quiz.famousquotequizbackend.adapter.driven.persistence

import com.quiz.famousquotequizbackend.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataUserRepository : JpaRepository<User, Long> {
    fun findByEmailIgnoreCase(email: String): User?
    fun existsByEmailIgnoreCase(email: String): Boolean
}
