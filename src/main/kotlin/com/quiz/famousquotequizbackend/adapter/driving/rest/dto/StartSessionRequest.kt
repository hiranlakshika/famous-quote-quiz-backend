package com.quiz.famousquotequizbackend.adapter.driving.rest.dto

import com.quiz.famousquotequizbackend.domain.quiz.QuizMode

data class StartSessionRequest(
    val mode: QuizMode = QuizMode.BINARY,
)
