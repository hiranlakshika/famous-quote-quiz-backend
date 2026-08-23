package com.quiz.famousquotequizbackend.quiz

import com.quiz.famousquotequizbackend.application.service.AuthService
import com.quiz.famousquotequizbackend.application.dto.auth.LoginRequest
import com.quiz.famousquotequizbackend.domain.quiz.QuizMode
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.post
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class QuizFlowIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val authService: AuthService,
) {

    private val token: String
        get() = authService.login(LoginRequest("demo@quiz.com", "password123")).token

    @Test
    fun `plays a full binary session`() {
        val bearer = token
        val sessionId = startSession(bearer, QuizMode.BINARY)

        repeat(9) {
            mockMvc.post("/api/quiz/sessions/$sessionId/answers") {
                header("Authorization", "Bearer $bearer")
                contentType = MediaType.APPLICATION_JSON
                content = """{"answer":"YES"}"""
            }.andExpect {
                status { isOk() }
                jsonPath("$.correctAuthor") { exists() }
                jsonPath("$.message") { exists() }
                jsonPath("$.sessionCompleted") { value(false) }
            }
        }

        mockMvc.post("/api/quiz/sessions/$sessionId/answers") {
            header("Authorization", "Bearer $bearer")
            contentType = MediaType.APPLICATION_JSON
            content = """{"answer":"YES"}"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.sessionCompleted") { value(true) }
            jsonPath("$.nextQuestion") { doesNotExist() }
        }
    }

    @Test
    fun `multiple choice questions contain three options`() {
        val bearer = token
        mockMvc.post("/api/quiz/sessions") {
            header("Authorization", "Bearer $bearer")
            contentType = MediaType.APPLICATION_JSON
            content = """{"mode":"MULTIPLE_CHOICE"}"""
        }.andExpect {
            status { isCreated() }
            jsonPath("$.currentQuestion.options.length()") { value(3) }
        }
    }

    @Test
    fun `rejects requests without a token`() {
        mockMvc.post("/api/quiz/sessions") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"mode":"BINARY"}"""
        }.andExpect { status { isUnauthorized() } }
    }

    private fun startSession(bearer: String, mode: QuizMode): String {
        val response = mockMvc.post("/api/quiz/sessions") {
            header("Authorization", "Bearer $bearer")
            contentType = MediaType.APPLICATION_JSON
            content = """{"mode":"${mode.name}"}"""
        }.andExpect { status { isCreated() } }.andReturn().response.contentAsString
        val sessionId = Regex("\"sessionId\":\"([^\"]+)\"").find(response)?.groupValues?.get(1)
        assertEquals(36, sessionId?.length)
        return checkNotNull(sessionId)
    }
}
