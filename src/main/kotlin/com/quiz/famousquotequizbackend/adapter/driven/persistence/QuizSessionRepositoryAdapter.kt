package com.quiz.famousquotequizbackend.adapter.driven.persistence

import com.quiz.famousquotequizbackend.application.port.driven.QuizSessionRepository
import com.quiz.famousquotequizbackend.domain.quiz.QuizSession
import org.springframework.stereotype.Component

@Component
class QuizSessionRepositoryAdapter(
    private val springDataQuizSessionRepository: SpringDataQuizSessionRepository
) : QuizSessionRepository {

    override fun save(session: QuizSession): QuizSession =
        springDataQuizSessionRepository.save(session)

    override fun findByIdAndUserId(id: String, userId: Long): QuizSession? =
        springDataQuizSessionRepository.findByIdAndUserId(id, userId)
}
