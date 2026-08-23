package com.quiz.famousquotequizbackend.quiz

data class AnswerResponse(
    val correct: Boolean,
    val correctAuthor: String,
    val message: String,
    val sessionCompleted: Boolean,
    val nextQuestion: QuestionResponse?,
)
