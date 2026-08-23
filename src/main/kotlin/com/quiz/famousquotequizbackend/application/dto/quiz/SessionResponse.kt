package com.quiz.famousquotequizbackend.application.dto.quiz

import com.quiz.famousquotequizbackend.domain.quiz.QuizMode
import com.quiz.famousquotequizbackend.domain.quiz.QuizSession

data class SessionResponse(
    val sessionId: String,
    val mode: QuizMode,
    val totalQuestions: Int,
    val answeredQuestions: Int,
    val completed: Boolean,
    val currentQuestion: QuestionResponse?,
) {
    companion object {
        fun from(session: QuizSession): SessionResponse = SessionResponse(
            sessionId = session.id,
            mode = session.mode,
            totalQuestions = session.questions.size,
            answeredQuestions = session.answeredCount(),
            completed = session.isCompleted,
            currentQuestion = session.currentQuestion()?.let { QuestionResponse.from(it, session.questions.size) },
        )
    }
}
