package com.quiz.famousquotequizbackend.application.exception

sealed class ApiException(val statusCode: Int, message: String) : RuntimeException(message)

class BadRequestException(message: String) : ApiException(400, message)

class UnauthorizedException(message: String) : ApiException(401, message)

class NotFoundException(message: String) : ApiException(404, message)
