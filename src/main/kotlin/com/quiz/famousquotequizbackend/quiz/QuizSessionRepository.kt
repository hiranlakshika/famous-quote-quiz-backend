package com.quiz.famousquotequizbackend.quiz

import org.springframework.data.jpa.repository.JpaRepository

interface QuizSessionRepository : JpaRepository<QuizSession, String> {
    fun findByIdAndUserId(id: String, userId: Long): QuizSession?
}
