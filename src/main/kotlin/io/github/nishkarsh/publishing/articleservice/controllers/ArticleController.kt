package io.github.nishkarsh.publishing.articleservice.controllers

import io.github.nishkarsh.publishing.articleservice.models.Article
import io.github.nishkarsh.publishing.articleservice.services.ArticleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController("/articles")
class ArticleController(private val service: ArticleService) {

	@PostMapping
	fun createArticle(article: Article): ResponseEntity<Unit> {
		val createdArticle = service.createArticle(article)
		return ResponseEntity.created(URI.create("/articles/${createdArticle.id}")).build()
	}
}