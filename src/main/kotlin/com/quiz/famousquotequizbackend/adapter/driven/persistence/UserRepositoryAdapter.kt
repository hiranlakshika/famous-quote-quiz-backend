package com.quiz.famousquotequizbackend.adapter.driven.persistence

import com.quiz.famousquotequizbackend.application.port.driven.UserRepository
import com.quiz.famousquotequizbackend.domain.user.User
import org.springframework.stereotype.Component

@Component
class UserRepositoryAdapter(
    private val springDataUserRepository: SpringDataUserRepository
) : UserRepository {

    override fun findByEmailIgnoreCase(email: String): User? =
        springDataUserRepository.findByEmailIgnoreCase(email)

    override fun existsByEmailIgnoreCase(email: String): Boolean =
        springDataUserRepository.existsByEmailIgnoreCase(email)

    override fun findById(id: Long): User? =
        springDataUserRepository.findById(id).orElse(null)

    override fun save(user: User): User =
        springDataUserRepository.save(user)
}
