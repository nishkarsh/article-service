package io.github.nishkarsh.publishing.articleservice.models

import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.MongoRegexCreator.INSTANCE
import org.springframework.data.mongodb.core.query.MongoRegexCreator.MatchMode.CONTAINING
import org.springframework.data.mongodb.core.query.Query
import java.time.ZonedDateTime

internal class SearchQueryBuilder(private val criteria: MutableSet<Criteria> = mutableSetOf()) {
	private fun containing(word: String) = INSTANCE.toRegularExpression(word, CONTAINING)!!

	fun authorNameLike(authorNameLike: String?) = apply {
		authorNameLike?.let {
			criteria.add(where(Article.AUTHORS).regex(containing(it), "i"))
		}
	}

	fun fromPublishDate(fromPublishDate: ZonedDateTime?) = apply {
		fromPublishDate?.let { criteria.add(where(Article.PUBLISH_DATE).gte(it)) }
	}

	fun toPublishDate(toPublishDate: ZonedDateTime?) = apply {
		toPublishDate?.let { criteria.add(where(Article.PUBLISH_DATE).lte(it)) }
	}

	fun keywordLike(keyword: String?) = apply {
		keyword?.let {
			criteria.add(where(Article.KEYWORDS).regex(containing(it), "i"))
		}
	}

	fun build(): Query {
		return when (criteria.size) {
			0 -> Query(Criteria())
			else -> Query(Criteria().andOperator(criteria))
		}
	}
}