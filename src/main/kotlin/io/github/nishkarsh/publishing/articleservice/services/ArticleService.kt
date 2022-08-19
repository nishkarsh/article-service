package io.github.nishkarsh.publishing.articleservice.services

import io.github.nishkarsh.publishing.articleservice.exceptions.ArticleNotFoundException
import io.github.nishkarsh.publishing.articleservice.models.Article
import io.github.nishkarsh.publishing.articleservice.repositories.ArticleRepository
import org.bson.types.ObjectId
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ArticleService(private val repository: ArticleRepository) {
	fun createArticle(article: Article): Article {
		return repository.insert(article)
	}

	fun getArticleById(id: ObjectId): Article? {
		return repository.findByIdOrNull(id)
	}

	fun updateArticle(article: Article): Article {
		when (repository.existsById(article.id!!)) {
			true -> return repository.save(article)
			else -> throw ArticleNotFoundException("Could not find article with ID: ${article.id}")
		}
	}

	fun deleteArticleById(id: ObjectId) {
		if (!repository.existsById(id)) {
			throw ArticleNotFoundException("Could not find article with ID: $id")
		}

		repository.deleteById(id)
	}
}
