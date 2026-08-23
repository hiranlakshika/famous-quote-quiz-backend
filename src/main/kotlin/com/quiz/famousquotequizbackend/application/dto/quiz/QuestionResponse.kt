package com.quiz.famousquotequizbackend.application.dto.quiz

import com.quiz.famousquotequizbackend.domain.quiz.QuizQuestion

data class QuestionResponse(
    val questionNumber: Int,
    val totalQuestions: Int,
    val quote: String,
    /** Only set in [QuizMode.BINARY] mode: the author the user has to confirm or reject. */
    val proposedAuthor: String?,
    val options: List<String>,
) {
    companion object {
        fun from(question: QuizQuestion, totalQuestions: Int): QuestionResponse = QuestionResponse(
            questionNumber = question.position,
            totalQuestions = totalQuestions,
            quote = question.quote.text,
            proposedAuthor = question.proposedAuthor,
            options = question.options.toList(),
        )
    }
}
