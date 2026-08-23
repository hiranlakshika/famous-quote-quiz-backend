package com.quiz.famousquotequizbackend.quiz

data class StartSessionRequest(
    val mode: QuizMode = QuizMode.BINARY,
)
