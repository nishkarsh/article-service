package io.github.nishkarsh.publishing.articleservice.exceptions

import org.springframework.web.bind.MethodArgumentNotValidException

fun MethodArgumentNotValidException.extraErrors(): List<String> {
	return mutableListOf<String>().apply {
		for (error in bindingResult.fieldErrors) {
			add(error.field + ": " + error.defaultMessage)
		}

		for (error in bindingResult.globalErrors) {
			add(error.objectName + ": " + error.defaultMessage)
		}
	}
}