package io.github.nishkarsh.publishing.articleservice.services

import io.github.nishkarsh.publishing.articleservice.exceptions.ArticleNotFoundException
import io.github.nishkarsh.publishing.articleservice.models.Article
import io.github.nishkarsh.publishing.articleservice.models.SearchCriteria
import io.github.nishkarsh.publishing.articleservice.models.SearchQueryBuilder
import io.github.nishkarsh.publishing.articleservice.repositories.ArticleRepository
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.repository.findByIdOrNull
import org.springframework.data.support.PageableExecutionUtils.getPage
import org.springframework.stereotype.Service

@Service
class ArticleService(
	private val repository: ArticleRepository, private val mongoTemplate: MongoTemplate
) {
	fun createArticle(article: Article): Article {
		return repository.insert(article)
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
				.build().with(pageable)
		}

		mongoTemplate.find<Article>(query).let { results ->
			return getPage(results, pageable) { mongoTemplate.count(query, Article::class.java) }
		}
	}

	fun updateArticle(article: Article): Article {
		when {
			repository.existsById(article.id!!) -> return repository.save(article)
			else -> throw ArticleNotFoundException("Could not find article with ID: ${article.id}")
		}
	}

	fun deleteArticleById(id: ObjectId) {
		when {
			repository.existsById(id) -> repository.deleteById(id)
			else -> throw ArticleNotFoundException("Could not find article with ID: $id")
		}
	}
}
