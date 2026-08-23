package com.quiz.famousquotequizbackend.adapter.driven.persistence

import com.quiz.famousquotequizbackend.domain.quote.Quote
import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataQuoteRepository : JpaRepository<Quote, Long> {
    fun existsByText(text: String): Boolean
}
