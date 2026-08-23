package com.quiz.famousquotequizbackend.auth

import com.quiz.famousquotequizbackend.application.service.AuthService
import com.quiz.famousquotequizbackend.application.dto.auth.LoginRequest

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import kotlin.test.assertNotEquals

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val authService: AuthService,
) {

    @Test
    fun `refresh returns a working access token and rotates the refresh token`() {
        val login = authService.login(LoginRequest("demo@quiz.com", "password123"))

        val refreshed = refreshExpectingOk(login.refreshToken)
        val rotated = valueOf(refreshed, "refreshToken")
        assertNotEquals(login.refreshToken, rotated)

        mockMvc.post("/api/quiz/sessions") {
            header("Authorization", "Bearer ${valueOf(refreshed, "token")}")
            contentType = MediaType.APPLICATION_JSON
            content = """{"mode":"BINARY"}"""
        }.andExpect { status { isCreated() } }

        refreshExpectingUnauthorized(login.refreshToken)
        refreshExpectingOk(rotated)
    }

    @Test
    fun `logout revokes the refresh token`() {
        val login = authService.login(LoginRequest("demo@quiz.com", "password123"))

        mockMvc.post("/api/auth/logout") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"refreshToken":"${login.refreshToken}"}"""
        }.andExpect { status { isNoContent() } }

        refreshExpectingUnauthorized(login.refreshToken)
    }

    @Test
    fun `rejects an unknown refresh token`() {
        refreshExpectingUnauthorized("not-a-refresh-token")
    }

    private fun refreshExpectingOk(refreshToken: String): String = refreshRequest(refreshToken)
        .andExpect { status { isOk() } }
        .andReturn().response.contentAsString

    private fun refreshExpectingUnauthorized(refreshToken: String) {
        refreshRequest(refreshToken).andExpect { status { isUnauthorized() } }
    }

    private fun refreshRequest(refreshToken: String) = mockMvc.post("/api/auth/refresh") {
        contentType = MediaType.APPLICATION_JSON
        content = """{"refreshToken":"$refreshToken"}"""
    }

    private fun valueOf(body: String, field: String): String =
        checkNotNull(Regex("\"$field\":\"([^\"]+)\"").find(body)).groupValues[1]
}
