package com.quiz.famousquotequizbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class FamousQuoteQuizBackendApplication

fun main(args: Array<String>) {
    runApplication<FamousQuoteQuizBackendApplication>(*args)
}
