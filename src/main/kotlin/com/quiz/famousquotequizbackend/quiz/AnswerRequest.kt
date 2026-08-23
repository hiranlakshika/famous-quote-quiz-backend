package com.quiz.famousquotequizbackend.quiz

import jakarta.validation.constraints.NotBlank

data class AnswerRequest(
    @field:NotBlank(message = "An answer is required")
    val answer: String,
)
