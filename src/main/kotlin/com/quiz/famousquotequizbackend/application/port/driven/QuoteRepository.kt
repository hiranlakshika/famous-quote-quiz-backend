package com.quiz.famousquotequizbackend.application.port.driven

import com.quiz.famousquotequizbackend.domain.quote.Quote

interface QuoteRepository {
    fun findAll(): List<Quote>
    fun existsByText(text: String): Boolean
    fun save(quote: Quote): Quote
    fun saveAll(quotes: List<Quote>): List<Quote>
}
