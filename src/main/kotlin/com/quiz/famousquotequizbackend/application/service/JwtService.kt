package com.quiz.famousquotequizbackend.application.service

import com.quiz.famousquotequizbackend.domain.user.User
import com.quiz.famousquotequizbackend.infrastructure.config.AuthProperties
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class JwtService(
    private val authProperties: AuthProperties,
    private val jwtEncoder: JwtEncoder,
) {

    fun issue(user: User): IssuedToken {
        val issuedAt = Instant.now()
        val expiresAt = issuedAt.plus(authProperties.tokenTtl)
        val claims = JwtClaimsSet.builder()
            .subject(requireNotNull(user.id).toString())
            .claim("email", user.email)
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .build()
        val header = JwsHeader.with(MacAlgorithm.HS256).build()
        return IssuedToken(
            value = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).tokenValue,
            expiresAt = expiresAt,
        )
    }
}
