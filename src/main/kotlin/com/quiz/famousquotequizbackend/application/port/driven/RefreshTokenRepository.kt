package com.quiz.famousquotequizbackend.application.port.driven

import com.quiz.famousquotequizbackend.domain.auth.RefreshToken

interface RefreshTokenRepository {
    fun findByValue(value: String): RefreshToken?
    fun delete(refreshToken: RefreshToken)
    fun deleteByValue(value: String)
    fun save(refreshToken: RefreshToken): RefreshToken
}
