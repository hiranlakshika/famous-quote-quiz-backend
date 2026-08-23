package com.quiz.famousquotequizbackend.adapter.driving.rest

import com.quiz.famousquotequizbackend.adapter.driving.rest.dto.AnswerRequest
import com.quiz.famousquotequizbackend.adapter.driving.rest.dto.StartSessionRequest
import com.quiz.famousquotequizbackend.adapter.driving.rest.security.CurrentUser
import com.quiz.famousquotequizbackend.application.dto.quiz.AnswerResponse
import com.quiz.famousquotequizbackend.application.dto.quiz.SessionResponse
import com.quiz.famousquotequizbackend.application.service.QuizService
import com.quiz.famousquotequizbackend.domain.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

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
