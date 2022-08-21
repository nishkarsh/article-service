package io.github.nishkarsh.publishing.articleservice.exceptions

data class ErrorResponse(val message: String?, val errors: List<String>? = null)