package com.quiz.famousquotequizbackend.adapter.driven.persistence

import com.quiz.famousquotequizbackend.domain.auth.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataRefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByValue(value: String): RefreshToken?
    fun deleteByValue(value: String)
}
