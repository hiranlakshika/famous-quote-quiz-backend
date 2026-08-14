package com.quiz.famousquotequizbackend.quiz

import com.quiz.famousquotequizbackend.common.BadRequestException
import com.quiz.famousquotequizbackend.common.NotFoundException
import com.quiz.famousquotequizbackend.quote.Quote
import com.quiz.famousquotequizbackend.quote.QuoteRepository
import com.quiz.famousquotequizbackend.user.User
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@ConfigurationProperties("quiz")
data class QuizProperties(
    val questionsPerSession: Int = 10,
    val multipleChoiceOptions: Int = 4,
)

@Service
@Transactional
class QuizService(
    private val quoteRepository: QuoteRepository,
    private val quizSessionRepository: QuizSessionRepository,
    private val quizProperties: QuizProperties,
) {

    fun startSession(user: User, mode: QuizMode): SessionResponse {
        val quotes = quoteRepository.findAll()
        val authors = quotes.map { it.author }.distinct()
        if (quotes.size < quizProperties.questionsPerSession || authors.size < quizProperties.multipleChoiceOptions) {
            throw BadRequestException("Not enough quotes available to start a quiz session")
        }

        val session = QuizSession(user = user, mode = mode)
        quotes.shuffled().take(quizProperties.questionsPerSession).forEachIndexed { index, quote ->
            session.questions += buildQuestion(session, quote, index + 1, authors)
        }
        return SessionResponse.from(quizSessionRepository.save(session))
    }

    fun answer(user: User, sessionId: String, answer: String): AnswerResponse {
        val session = findSession(user, sessionId)
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

    private fun findSession(user: User, sessionId: String): QuizSession =
        quizSessionRepository.findByIdAndUserId(sessionId, requireNotNull(user.id))
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
                val distractors = otherAuthors.shuffled().take(quizProperties.multipleChoiceOptions - 1)
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