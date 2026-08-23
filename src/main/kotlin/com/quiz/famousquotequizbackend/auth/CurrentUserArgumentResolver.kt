package com.quiz.famousquotequizbackend.auth

import com.quiz.famousquotequizbackend.common.UnauthorizedException
import com.quiz.famousquotequizbackend.user.User
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/** Turns the validated JWT of the current request into the [User] it belongs to. */
@Component
class CurrentUserArgumentResolver(private val authService: AuthService) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.hasParameterAnnotation(CurrentUser::class.java) && parameter.parameterType == User::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): User {
        val jwt = SecurityContextHolder.getContext().authentication?.principal as? Jwt
            ?: throw UnauthorizedException("Missing authentication token")
        return authService.userOf(jwt.subject)
    }
}
