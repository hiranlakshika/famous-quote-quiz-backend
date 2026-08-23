package com.quiz.famousquotequizbackend.adapter.driving.rest.security

import com.quiz.famousquotequizbackend.application.exception.UnauthorizedException
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/** Turns the validated JWT of the current request into the user id in its subject. */
@Component
class CurrentUserArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.hasParameterAnnotation(CurrentUser::class.java) &&
            (parameter.parameterType == Long::class.javaPrimitiveType ||
                parameter.parameterType == Long::class.javaObjectType)

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Long {
        val jwt = SecurityContextHolder.getContext().authentication?.principal as? Jwt
            ?: throw UnauthorizedException("Missing authentication token")
        return jwt.subject?.toLongOrNull()
            ?: throw UnauthorizedException("Invalid or expired token")
    }
}
