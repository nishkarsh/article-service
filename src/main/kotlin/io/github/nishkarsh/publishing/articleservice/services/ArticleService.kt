package io.github.nishkarsh.publishing.articleservice.services

import io.github.nishkarsh.publishing.articleservice.exceptions.ArticleNotFoundException
import io.github.nishkarsh.publishing.articleservice.models.Article
import io.github.nishkarsh.publishing.articleservice.models.SearchCriteria
import io.github.nishkarsh.publishing.articleservice.models.SearchQueryBuilder
import io.github.nishkarsh.publishing.articleservice.repositories.ArticleRepository
import io.github.nishkarsh.publishing.articleservice.repositories.find
import mu.KotlinLogging.logger
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

private val logger = logger {}

@Service
class ArticleService(private val repository: ArticleRepository, private val mongoTemplate: MongoTemplate) {
	fun createArticle(article: Article): Article {
		return repository.insert(article).also {
			logger.debug { "Created Article with ID: ${it.id}" }
		}
	}

	fun getArticleById(id: ObjectId): Article? {
		return repository.findByIdOrNull(id)
	}

	fun getArticles(searchCriteria: SearchCriteria, pageable: Pageable): Page<Article> {
		val query = with(searchCriteria) {
			SearchQueryBuilder()
				.authorNameLike(author)
				.fromPublishDate(fromPublishDate)
				.toPublishDate(toPublishDate)
				.keywordLike(keyword)
				.build()
		}

		logger.trace { "Generated query to search articles: $query" }

		return mongoTemplate.find(query, pageable)
	}

	fun updateArticle(article: Article): Article {
		logger.debug { "Updating Article with ID: ${article.id}" }

		when {
			repository.existsById(article.id!!) -> return repository.save(article)
			else -> throw ArticleNotFoundException("Could not find article with ID: ${article.id}")
		}
	}

	fun deleteArticleById(id: ObjectId) {
		logger.debug { "Deleting Article with ID: $id" }

		when {
			repository.existsById(id) -> repository.deleteById(id)
			else -> throw ArticleNotFoundException("Could not find article with ID: $id")
		}
	}
}
