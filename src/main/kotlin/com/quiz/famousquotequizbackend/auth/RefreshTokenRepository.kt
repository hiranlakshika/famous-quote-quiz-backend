package com.quiz.famousquotequizbackend.auth

import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByValue(value: String): RefreshToken?
    fun deleteByValue(value: String)
}
