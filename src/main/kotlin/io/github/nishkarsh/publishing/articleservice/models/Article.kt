package io.github.nishkarsh.publishing.articleservice.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.ZonedDateTime

@Document("articles")
data class Article(
	@JsonProperty("id", access = READ_ONLY)
	val id: ObjectId?,

	@JsonProperty("header")
	val header: String,

	@JsonProperty("shortDescription")
	val shortDescription: String,

	@JsonProperty("text")
	val text: String,

	@JsonProperty("publishDate")
	val publishDate: ZonedDateTime,

	@JsonProperty("authors")
	val authors: ArrayList<String>,

	@JsonProperty("keywords")
	val keywords: ArrayList<String>,
)
