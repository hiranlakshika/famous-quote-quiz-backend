package com.quiz.famousquotequizbackend.infrastructure.config

import com.quiz.famousquotequizbackend.application.port.driven.PasswordHasher
import com.quiz.famousquotequizbackend.application.port.driven.QuoteRepository
import com.quiz.famousquotequizbackend.application.port.driven.UserRepository
import com.quiz.famousquotequizbackend.domain.quote.Quote
import com.quiz.famousquotequizbackend.domain.user.User
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class DataSeeder(
    private val quoteRepository: QuoteRepository,
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        seedQuotes()
        seedDemoUser()
    }

    private fun seedQuotes() {
        val newQuotes = QUOTES.filterNot { quoteRepository.existsByText(it.first) }
            .map { Quote(text = it.first, author = it.second) }
        quoteRepository.saveAll(newQuotes)
    }

    private fun seedDemoUser() {
        if (userRepository.existsByEmailIgnoreCase(DEMO_EMAIL)) return
        userRepository.save(
            User(
                email = DEMO_EMAIL,
                passwordHash = passwordHasher.encode(DEMO_PASSWORD),
                displayName = "Demo User",
            )
        )
    }

    private companion object {
        const val DEMO_EMAIL = "demo@quiz.com"
        const val DEMO_PASSWORD = "password123"

        val QUOTES = listOf(
            "The only thing we have to fear is fear itself." to "Franklin D. Roosevelt",
            "I think, therefore I am." to "René Descartes",
            "That's one small step for a man, one giant leap for mankind." to "Neil Armstrong",
            "Ask not what your country can do for you, ask what you can do for your country." to "John F. Kennedy",
            "I have a dream." to "Martin Luther King Jr.",
            "Imagination is more important than knowledge." to "Albert Einstein",
            "The unexamined life is not worth living." to "Socrates",
            "To be, or not to be, that is the question." to "William Shakespeare",
            "In the middle of difficulty lies opportunity." to "Albert Einstein",
            "Be the change that you wish to see in the world." to "Mahatma Gandhi",
            "An eye for an eye will only make the whole world blind." to "Mahatma Gandhi",
            "The only true wisdom is in knowing you know nothing." to "Socrates",
            "Genius is one percent inspiration and ninety-nine percent perspiration." to "Thomas Edison",
            "I have not failed. I've just found 10,000 ways that won't work." to "Thomas Edison",
            "The greatest glory in living lies not in never falling, but in rising every time we fall." to "Nelson Mandela",
            "Education is the most powerful weapon which you can use to change the world." to "Nelson Mandela",
            "It always seems impossible until it's done." to "Nelson Mandela",
            "Success is not final, failure is not fatal: it is the courage to continue that counts." to "Winston Churchill",
            "If you're going through hell, keep going." to "Winston Churchill",
            "Stay hungry, stay foolish." to "Steve Jobs",
            "Innovation distinguishes between a leader and a follower." to "Steve Jobs",
            "The future belongs to those who believe in the beauty of their dreams." to "Eleanor Roosevelt",
            "No one can make you feel inferior without your consent." to "Eleanor Roosevelt",
            "Whether you think you can or you think you can't, you're right." to "Henry Ford",
            "Life is what happens when you're busy making other plans." to "John Lennon",
            "Happiness is not something ready made. It comes from your own actions." to "Dalai Lama",
            "Two things are infinite: the universe and human stupidity." to "Albert Einstein",
            "The journey of a thousand miles begins with one step." to "Lao Tzu",
            "Knowing yourself is the beginning of all wisdom." to "Aristotle",
            "We are what we repeatedly do. Excellence, then, is not an act, but a habit." to "Aristotle",
            "A room without books is like a body without a soul." to "Marcus Tullius Cicero",
            "The best way to predict the future is to invent it." to "Alan Kay",
        )
    }
}
