package com.quiz.famousquotequizbackend.application.port.driving

import com.quiz.famousquotequizbackend.application.dto.quiz.AnswerResponse
import com.quiz.famousquotequizbackend.application.dto.quiz.SessionResponse
import com.quiz.famousquotequizbackend.domain.quiz.QuizMode

interface QuizUseCase {
    fun startSession(userId: Long, mode: QuizMode): SessionResponse
    fun answer(userId: Long, sessionId: String, answer: String): AnswerResponse
}
