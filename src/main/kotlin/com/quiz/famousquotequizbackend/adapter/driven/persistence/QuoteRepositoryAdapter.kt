package com.quiz.famousquotequizbackend.adapter.driven.persistence

import com.quiz.famousquotequizbackend.application.port.driven.QuoteRepository
import com.quiz.famousquotequizbackend.domain.quote.Quote
import org.springframework.stereotype.Component

@Component
class QuoteRepositoryAdapter(
    private val springDataQuoteRepository: SpringDataQuoteRepository
) : QuoteRepository {

    override fun findAll(): List<Quote> =
        springDataQuoteRepository.findAll()

    override fun existsByText(text: String): Boolean =
        springDataQuoteRepository.existsByText(text)

    override fun save(quote: Quote): Quote =
        springDataQuoteRepository.save(quote)

    override fun saveAll(quotes: List<Quote>): List<Quote> =
        springDataQuoteRepository.saveAll(quotes)
}
