package com.quiz.famousquotequizbackend.quiz

import com.quiz.famousquotequizbackend.quote.Quote
import com.quiz.famousquotequizbackend.user.User
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

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
