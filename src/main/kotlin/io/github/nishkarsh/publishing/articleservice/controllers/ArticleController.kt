package io.github.nishkarsh.publishing.articleservice.controllers

import io.github.nishkarsh.publishing.articleservice.exceptions.ArticleNotFoundException
import io.github.nishkarsh.publishing.articleservice.models.Article
import io.github.nishkarsh.publishing.articleservice.services.ArticleService
import org.bson.types.ObjectId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/articles")
class ArticleController(private val service: ArticleService) {

	@PostMapping
	fun createArticle(@RequestBody article: Article): ResponseEntity<Unit> {
		val createdArticle = service.createArticle(article)
		return ResponseEntity.created(URI.create("/articles/${createdArticle.id}")).build()
	}

	@GetMapping("/{id}")
	fun getArticleById(@PathVariable id: ObjectId): ResponseEntity<Article> {
		return service.getArticleById(id)?.let { ResponseEntity.ok(it) }
			?: throw ArticleNotFoundException("Could not find article with ID: $id")
	}

	@GetMapping
	fun searchArticles(@RequestParam allParams: Map<String, String>): ResponseEntity<List<Article>> {
		return ResponseEntity.ok(service.getArticles(allParams))
	}

	@PutMapping("/{id}")
	fun updateArticle(@PathVariable id: ObjectId, @RequestBody article: Article): ResponseEntity<Unit> {
		service.updateArticle(article.copy(id = id))
		return ResponseEntity.noContent().build()
	}

	@DeleteMapping("/{id}")
	fun deleteById(@PathVariable id: ObjectId): ResponseEntity<Unit> {
		service.deleteArticleById(id)
		return ResponseEntity.noContent().build()
	}
}