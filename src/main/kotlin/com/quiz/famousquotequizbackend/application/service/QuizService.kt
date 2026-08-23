package com.quiz.famousquotequizbackend.application.service

import com.quiz.famousquotequizbackend.application.dto.quiz.AnswerResponse
import com.quiz.famousquotequizbackend.application.dto.quiz.QuestionResponse
import com.quiz.famousquotequizbackend.application.dto.quiz.SessionResponse
import com.quiz.famousquotequizbackend.application.exception.BadRequestException
import com.quiz.famousquotequizbackend.application.exception.NotFoundException
import com.quiz.famousquotequizbackend.application.exception.UnauthorizedException
import com.quiz.famousquotequizbackend.application.port.driven.QuizSessionRepository
import com.quiz.famousquotequizbackend.application.port.driven.QuizSettings
import com.quiz.famousquotequizbackend.application.port.driven.QuoteRepository
import com.quiz.famousquotequizbackend.application.port.driven.UserRepository
import com.quiz.famousquotequizbackend.application.port.driving.QuizUseCase
import com.quiz.famousquotequizbackend.domain.quiz.BinaryAnswer
import com.quiz.famousquotequizbackend.domain.quiz.QuizMode
import com.quiz.famousquotequizbackend.domain.quiz.QuizQuestion
import com.quiz.famousquotequizbackend.domain.quiz.QuizSession
import com.quiz.famousquotequizbackend.domain.quote.Quote
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
@Transactional
class QuizService(
    private val quoteRepository: QuoteRepository,
    private val quizSessionRepository: QuizSessionRepository,
    private val userRepository: UserRepository,
    private val quizSettings: QuizSettings,
) : QuizUseCase {

    override fun startSession(userId: Long, mode: QuizMode): SessionResponse {
        val user = userRepository.findById(userId)
            ?: throw UnauthorizedException("Invalid or expired token")
        val quotes = quoteRepository.findAll()
        val authors = quotes.map { it.author }.distinct()
        if (quotes.size < quizSettings.questionsPerSession || authors.size < quizSettings.multipleChoiceOptions) {
            throw BadRequestException("Not enough quotes available to start a quiz session")
        }

        val session = QuizSession(user = user, mode = mode)
        quotes.shuffled().take(quizSettings.questionsPerSession).forEachIndexed { index, quote ->
            session.questions += buildQuestion(session, quote, index + 1, authors)
        }
        return SessionResponse.from(quizSessionRepository.save(session))
    }

    override fun answer(userId: Long, sessionId: String, answer: String): AnswerResponse {
        val session = findSession(userId, sessionId)
        val question = session.currentQuestion()
            ?: throw BadRequestException("The quiz session is already completed")

        val submitted = question.options.firstOrNull { it.equals(answer.trim(), ignoreCase = true) }
            ?: throw BadRequestException("Answer must be one of: ${question.options.joinToString()}")

        val correct = submitted.equals(question.correctAnswer, ignoreCase = true)
        question.selectedAnswer = submitted
        question.correct = correct
        question.answeredAt = Instant.now()

        if (session.currentQuestion() == null) {
            session.completedAt = Instant.now()
        }
        quizSessionRepository.save(session)

        val author = question.quote.author
        return AnswerResponse(
            correct = correct,
            correctAuthor = author,
            message = if (correct) {
                "Correct! The right answer is: $author"
            } else {
                "Sorry, you are wrong! The right answer is: $author"
            },
            sessionCompleted = session.isCompleted,
            nextQuestion = session.currentQuestion()?.let { QuestionResponse.from(it, session.questions.size) },
        )
    }

    private fun findSession(userId: Long, sessionId: String): QuizSession =
        quizSessionRepository.findByIdAndUserId(sessionId, userId)
            ?: throw NotFoundException("Quiz session not found")

    private fun buildQuestion(
        session: QuizSession,
        quote: Quote,
        position: Int,
        authors: List<String>,
    ): QuizQuestion {
        val otherAuthors = authors.filter { it != quote.author }
        return when (session.mode) {
            QuizMode.BINARY -> QuizQuestion(
                session = session,
                quote = quote,
                position = position,
                proposedAuthor = if (listOf(true, false).random()) quote.author else otherAuthors.random(),
                options = BinaryAnswer.entries.map { it.name }.toMutableList(),
            )

            QuizMode.MULTIPLE_CHOICE -> {
                val distractors = otherAuthors.shuffled().take(quizSettings.multipleChoiceOptions - 1)
                QuizQuestion(
                    session = session,
                    quote = quote,
                    position = position,
                    options = (distractors + quote.author).shuffled().toMutableList(),
                )
            }
        }
    }
}
