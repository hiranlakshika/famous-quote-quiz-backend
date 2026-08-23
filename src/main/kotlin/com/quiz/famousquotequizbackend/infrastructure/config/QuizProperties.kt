package com.quiz.famousquotequizbackend.infrastructure.config

import com.quiz.famousquotequizbackend.application.port.driven.QuizSettings
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("quiz")
data class QuizProperties(
    override val questionsPerSession: Int = 10,
    override val multipleChoiceOptions: Int = 3,
) : QuizSettings
