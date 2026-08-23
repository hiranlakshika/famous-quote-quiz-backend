package com.quiz.famousquotequizbackend.application.port.driven

interface QuizSettings {
    val questionsPerSession: Int
    val multipleChoiceOptions: Int
}
