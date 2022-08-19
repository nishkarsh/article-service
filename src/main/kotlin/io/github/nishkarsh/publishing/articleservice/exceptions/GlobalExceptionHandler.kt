package io.github.nishkarsh.publishing.articleservice.exceptions

import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

	@ExceptionHandler(value = [ArticleNotFoundException::class])
	protected fun handleNotFoundError(exception: ArticleNotFoundException): ResponseEntity<ErrorResponse> {
		return ResponseEntity.status(NOT_FOUND)
			.body(ErrorResponse(exception.message))
	}
}

data class ArticleNotFoundException(override val message: String) : Throwable(message)