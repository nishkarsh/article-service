package io.github.nishkarsh.publishing.articleservice.models

import org.springframework.format.annotation.DateTimeFormat
import java.time.ZonedDateTime

data class SearchCriteria(
	val author: String?,

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	val fromPublishDate: ZonedDateTime?,

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	val toPublishDate: ZonedDateTime?,

	val keyword: String?
)