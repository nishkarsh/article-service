package io.github.nishkarsh.publishing.articleservice.exceptions

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

	@ExceptionHandler(value = [ArticleNotFoundException::class])
	protected fun handleNotFoundError(exception: ArticleNotFoundException): ResponseEntity<ErrorResponse> {
		return ResponseEntity.status(NOT_FOUND)
			.body(ErrorResponse(exception.message))
	}

	@ExceptionHandler(value = [Exception::class])
	protected fun handleInternalServerError(ex: Exception): ResponseEntity<ErrorResponse> {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ErrorResponse(ex.message))
	}

	override fun handleExceptionInternal(
		exception: Exception, body: Any?, headers: HttpHeaders, status: HttpStatus, request: WebRequest
	): ResponseEntity<Any> {
		return super.handleExceptionInternal(exception, ErrorResponse(exception.message), headers, status, request)
	}
}

data class ArticleNotFoundException(override val message: String) : RuntimeException(message)