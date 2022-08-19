package io.github.nishkarsh.publishing.articleservice.models

import java.time.ZonedDateTime

data class SearchCriteria(
	val author: String,
	val fromPublishDate: ZonedDateTime,
	val toPublishDate: ZonedDateTime,
	val keywords: List<String>
)