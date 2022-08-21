package io.github.nishkarsh.publishing.articleservice.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.ZonedDateTime
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

@Document(Article.COLLECTION)
data class Article(
	@JsonProperty(ID, access = READ_ONLY)
	val id: ObjectId?,

	@Field(HEADER)
	@JsonProperty(HEADER)
	@field:NotEmpty @field:Size(min = 10, max = 100)
	val header: String,

	@Field(SHORT_DESCRIPTION)
	@JsonProperty(SHORT_DESCRIPTION)
	@field:NotEmpty @field:Size(min = 50, max = 200)
	val shortDescription: String,

	@Field(TEXT)
	@JsonProperty(TEXT)
	@field:NotEmpty @field:Size(min = 100, max = 1000)
	val text: String,

	@Indexed
	@Field(PUBLISH_DATE)
	@JsonProperty(PUBLISH_DATE)
	val publishDate: ZonedDateTime,

	@Indexed
	@Field(AUTHORS)
	@JsonProperty(AUTHORS)
	val authors: ArrayList<String>?,

	@Indexed
	@Field(KEYWORDS)
	@JsonProperty(KEYWORDS)
	val keywords: ArrayList<String>?,
) {
	companion object {
		const val COLLECTION = "articles"
		const val ID = "id"
		const val HEADER = "header"
		const val SHORT_DESCRIPTION = "shortDescription"
		const val TEXT = "text"
		const val AUTHORS = "authors"
		const val PUBLISH_DATE = "publishDate"
		const val KEYWORDS = "keywords"
	}
}
