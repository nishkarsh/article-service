package io.github.nishkarsh.publishing.articleservice.controllers

import io.github.nishkarsh.publishing.articleservice.exceptions.ArticleNotFoundException
import io.github.nishkarsh.publishing.articleservice.models.Article
import io.github.nishkarsh.publishing.articleservice.models.SearchCriteria
import io.github.nishkarsh.publishing.articleservice.services.ArticleService
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.validation.Valid

@RestController
@RequestMapping("/articles")
class ArticleController(private val service: ArticleService) {

	@PostMapping
	fun createArticle(@Valid @RequestBody article: Article): ResponseEntity<Unit> {
		val createdArticle = service.createArticle(article)
		return ResponseEntity.created(URI.create("/articles/${createdArticle.id}")).build()
	}

	@GetMapping
	fun searchArticles(criteria: SearchCriteria, pageable: Pageable): ResponseEntity<Page<Article>> {
		return ResponseEntity.ok(service.getArticles(criteria, pageable))
	}

	@GetMapping("/{id}")
	fun getArticleById(@PathVariable id: ObjectId): ResponseEntity<Article> {
		return service.getArticleById(id)?.let { ResponseEntity.ok(it) }
			?: throw ArticleNotFoundException("Could not find article with ID: $id")
	}

	@PutMapping("/{id}")
	fun updateArticle(@PathVariable id: ObjectId, @RequestBody @Valid article: Article): ResponseEntity<Unit> {
		service.updateArticle(article.copy(id = id))
		return ResponseEntity.noContent().build()
	}

	@DeleteMapping("/{id}")
	fun deleteArticleById(@PathVariable id: ObjectId): ResponseEntity<Unit> {
		service.deleteArticleById(id)
		return ResponseEntity.noContent().build()
	}
}