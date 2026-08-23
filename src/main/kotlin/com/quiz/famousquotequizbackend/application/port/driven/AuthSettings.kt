package com.quiz.famousquotequizbackend.application.port.driven

import java.time.Duration

interface AuthSettings {
    val refreshTokenTtl: Duration
}
