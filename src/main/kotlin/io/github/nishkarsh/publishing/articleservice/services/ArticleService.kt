package io.github.nishkarsh.publishing.articleservice.services

import io.github.nishkarsh.publishing.articleservice.models.Article
import io.github.nishkarsh.publishing.articleservice.repositories.ArticleRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class ArticleService(private val repository: ArticleRepository) {
	fun createArticle(article: Article): Article {
		return repository.insert(article)
	}

	fun getArticleById(id: ObjectId): Article? {
		TODO("Not yet implemented")
	}
}
