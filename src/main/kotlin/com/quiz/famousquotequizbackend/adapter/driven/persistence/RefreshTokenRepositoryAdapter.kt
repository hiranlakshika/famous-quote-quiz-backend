package com.quiz.famousquotequizbackend.adapter.driven.persistence

import com.quiz.famousquotequizbackend.application.port.driven.RefreshTokenRepository
import com.quiz.famousquotequizbackend.domain.auth.RefreshToken
import org.springframework.stereotype.Component

@Component
class RefreshTokenRepositoryAdapter(
    private val springDataRefreshTokenRepository: SpringDataRefreshTokenRepository
) : RefreshTokenRepository {

    override fun findByValue(value: String): RefreshToken? =
        springDataRefreshTokenRepository.findByValue(value)

    override fun delete(refreshToken: RefreshToken) =
        springDataRefreshTokenRepository.delete(refreshToken)

    override fun deleteByValue(value: String) =
        springDataRefreshTokenRepository.deleteByValue(value)

    override fun save(refreshToken: RefreshToken): RefreshToken =
        springDataRefreshTokenRepository.save(refreshToken)
}
