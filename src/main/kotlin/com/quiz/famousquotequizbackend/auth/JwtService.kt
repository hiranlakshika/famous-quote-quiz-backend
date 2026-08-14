package com.quiz.famousquotequizbackend.auth

import com.quiz.famousquotequizbackend.user.User
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant

@ConfigurationProperties("auth")
data class AuthProperties(
    /** HMAC key used to sign the JWTs, at least 32 characters long. */
    val secret: String,
    val tokenTtl: Duration = Duration.ofMinutes(15),
    val refreshTokenTtl: Duration = Duration.ofDays(30),
)

data class IssuedToken(
    val value: String,
    val expiresAt: Instant,
)

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
