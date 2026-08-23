package com.quiz.famousquotequizbackend.quiz

import com.quiz.famousquotequizbackend.quote.Quote
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OrderColumn
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "quiz_questions")
class QuizQuestion(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id")
    var session: QuizSession,

    @ManyToOne(optional = false)
    @JoinColumn(name = "quote_id")
    var quote: Quote,

    @Column(nullable = false)
    var position: Int,

    /** The author the user is asked about in [QuizMode.BINARY] mode; may or may not be the real one. */
    var proposedAuthor: String? = null,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "quiz_question_options", joinColumns = [JoinColumn(name = "question_id")])
    @OrderColumn(name = "option_index")
    @Column(name = "option_value")
    var options: MutableList<String> = mutableListOf(),

    var selectedAnswer: String? = null,

    var correct: Boolean? = null,

    var answeredAt: Instant? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
) {
    val isAnswered: Boolean
        get() = answeredAt != null

    val correctAnswer: String
        get() = when (session.mode) {
            QuizMode.BINARY -> if (proposedAuthor == quote.author) BinaryAnswer.YES.name else BinaryAnswer.NO.name
            QuizMode.MULTIPLE_CHOICE -> quote.author
        }
}
