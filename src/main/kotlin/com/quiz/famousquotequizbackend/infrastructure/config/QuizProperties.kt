package com.quiz.famousquotequizbackend.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("quiz")
data class QuizProperties(
    val questionsPerSession: Int = 10,
    val multipleChoiceOptions: Int = 3,
)
