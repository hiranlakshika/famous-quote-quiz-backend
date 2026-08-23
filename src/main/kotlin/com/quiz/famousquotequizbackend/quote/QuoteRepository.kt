package com.quiz.famousquotequizbackend.quote

import org.springframework.data.jpa.repository.JpaRepository

interface QuoteRepository : JpaRepository<Quote, Long> {
    fun existsByText(text: String): Boolean
}
