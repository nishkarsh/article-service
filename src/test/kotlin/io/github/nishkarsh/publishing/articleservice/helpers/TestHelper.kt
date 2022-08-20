package io.github.nishkarsh.publishing.articleservice.helpers

import io.github.nishkarsh.publishing.articleservice.models.Article
import org.bson.types.ObjectId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

object TestHelper {
	fun toUtcAndTruncatedToSeconds(date: ZonedDateTime): ZonedDateTime {
		return date.withZoneSameInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
	}

	fun truncateToSeconds(date: ZonedDateTime): ZonedDateTime {
		return date.truncatedTo(ChronoUnit.SECONDS)
	}

	fun getSampleArticlesWithPublishDates(juneOne2022: ZonedDateTime, julyTwo2022: ZonedDateTime): List<Article> {
		return listOf(
			Article(
				ObjectId(), "Article One", "That's all there is first",
				"Whoa! You are really interested in reading Article One",
				juneOne2022, arrayListOf("Nishkarsh Sharma", "Paras Khulbe", "Jessica"),
				arrayListOf("one", "article", "first", "june")
			), Article(
				ObjectId(), "Article Two", "That's all there is second",
				"Whoa! You are really interested in reading Article Two",
				julyTwo2022, arrayListOf("Nishkarsh Sharma", "Jessica"),
				arrayListOf("two", "article", "second", "july")
			), Article(
				ObjectId(), "Article Three", "That's all there is third",
				"Whoa! You are really interested in reading Article Three",
				julyTwo2022, arrayListOf("Paras"), arrayListOf("three", "article", "third", "july")
			)
		)
	}
}