package com.quiz.famousquotequizbackend.infrastructure.common

data class ErrorResponse(
    val status: Int,
    val message: String,
    val fieldErrors: Map<String, String> = emptyMap(),
)
