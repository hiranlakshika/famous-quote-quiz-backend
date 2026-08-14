package com.quiz.famousquotequizbackend.quiz

import com.quiz.famousquotequizbackend.quote.Quote
import com.quiz.famousquotequizbackend.user.User
import jakarta.persistence.CascadeType
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.OrderColumn
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.UUID

enum class QuizMode {
    BINARY,
    MULTIPLE_CHOICE,
}

enum class BinaryAnswer {
    YES,
    NO,
}

@Entity
@Table(name = "quiz_sessions")
class QuizSession(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    var user: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var mode: QuizMode,

    @Column(nullable = false)
    var startedAt: Instant = Instant.now(),

    var completedAt: Instant? = null,

    @OneToMany(mappedBy = "session", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("position ASC")
    var questions: MutableList<QuizQuestion> = mutableListOf(),

    @Id
    var id: String = UUID.randomUUID().toString(),
) {
    val isCompleted: Boolean
        get() = completedAt != null

    fun currentQuestion(): QuizQuestion? = questions.firstOrNull { !it.isAnswered }

    fun correctAnswerCount(): Int = questions.count { it.correct == true }

    fun answeredCount(): Int = questions.count { it.isAnswered }
}

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

interface QuizSessionRepository : JpaRepository<QuizSession, String> {
    fun findByIdAndUserId(id: String, userId: Long): QuizSession?
}
