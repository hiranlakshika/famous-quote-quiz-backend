package com.quiz.famousquotequizbackend.application.port.driven

import com.quiz.famousquotequizbackend.domain.quiz.QuizSession

interface QuizSessionRepository {
    fun save(session: QuizSession): QuizSession
    fun findByIdAndUserId(id: String, userId: Long): QuizSession?
}
