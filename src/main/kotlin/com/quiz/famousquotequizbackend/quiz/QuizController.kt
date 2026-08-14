package com.quiz.famousquotequizbackend.quiz

import com.quiz.famousquotequizbackend.auth.CurrentUser
import com.quiz.famousquotequizbackend.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

data class StartSessionRequest(
    val mode: QuizMode = QuizMode.BINARY,
)

data class AnswerRequest(
    @field:NotBlank(message = "An answer is required")
    val answer: String,
)

data class QuestionResponse(
    val questionNumber: Int,
    val totalQuestions: Int,
    val quote: String,
    /** Only set in [QuizMode.BINARY] mode: the author the user has to confirm or reject. */
    val proposedAuthor: String?,
    val options: List<String>,
) {
    companion object {
        fun from(question: QuizQuestion, totalQuestions: Int): QuestionResponse = QuestionResponse(
            questionNumber = question.position,
            totalQuestions = totalQuestions,
            quote = question.quote.text,
            proposedAuthor = question.proposedAuthor,
            options = question.options.toList(),
        )
    }
}

data class SessionResponse(
    val sessionId: String,
    val mode: QuizMode,
    val totalQuestions: Int,
    val answeredQuestions: Int,
    val completed: Boolean,
    val currentQuestion: QuestionResponse?,
) {
    companion object {
        fun from(session: QuizSession): SessionResponse = SessionResponse(
            sessionId = session.id,
            mode = session.mode,
            totalQuestions = session.questions.size,
            answeredQuestions = session.answeredCount(),
            completed = session.isCompleted,
            currentQuestion = session.currentQuestion()?.let { QuestionResponse.from(it, session.questions.size) },
        )
    }
}

data class AnswerResponse(
    val correct: Boolean,
    val correctAuthor: String,
    val message: String,
    val sessionCompleted: Boolean,
    val nextQuestion: QuestionResponse?,
)

@RestController
@RequestMapping("/api/quiz/sessions")
@Tag(name = "Quiz")
class QuizController(private val quizService: QuizService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Start a session of 10 questions in the given mode")
    fun start(@CurrentUser user: User, @RequestBody request: StartSessionRequest): SessionResponse =
        quizService.startSession(user, request.mode)

    @PostMapping("/{sessionId}/answers")
    @Operation(summary = "Answer the current question and get the correct author back")
    fun answer(
        @CurrentUser user: User,
        @PathVariable sessionId: String,
        @Valid @RequestBody request: AnswerRequest,
    ): AnswerResponse = quizService.answer(user, sessionId, request.answer)
}
