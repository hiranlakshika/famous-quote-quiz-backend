package com.quiz.famousquotequizbackend.common

import org.springframework.http.HttpStatus

sealed class ApiException(val status: HttpStatus, message: String) : RuntimeException(message)

class BadRequestException(message: String) : ApiException(HttpStatus.BAD_REQUEST, message)

class UnauthorizedException(message: String) : ApiException(HttpStatus.UNAUTHORIZED, message)

class NotFoundException(message: String) : ApiException(HttpStatus.NOT_FOUND, message)
