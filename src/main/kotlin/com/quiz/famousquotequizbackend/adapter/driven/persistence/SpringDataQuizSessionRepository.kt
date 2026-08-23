package com.quiz.famousquotequizbackend.adapter.driven.persistence

import com.quiz.famousquotequizbackend.domain.quiz.QuizSession
import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataQuizSessionRepository : JpaRepository<QuizSession, String> {
    fun findByIdAndUserId(id: String, userId: Long): QuizSession?
}
