package com.quiz.famousquotequizbackend.user

import java.time.Instant

data class UserResponse(
    val id: Long,
    val email: String,
    val displayName: String,
    val memberSince: Instant,
) {
    companion object {
        fun from(user: User): UserResponse = UserResponse(
            id = requireNotNull(user.id),
            email = user.email,
            displayName = user.displayName,
            memberSince = user.createdAt,
        )
    }
}
