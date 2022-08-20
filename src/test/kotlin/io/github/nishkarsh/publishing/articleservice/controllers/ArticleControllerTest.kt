package io.github.nishkarsh.publishing.articleservice.controllers

import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import io.github.nishkarsh.publishing.articleservice.exceptions.ArticleNotFoundException
import io.github.nishkarsh.publishing.articleservice.models.Article
import io.github.nishkarsh.publishing.articleservice.models.SearchCriteria
import io.github.nishkarsh.publishing.articleservice.services.ArticleService
import org.bson.types.ObjectId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.stub
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import java.net.URI

@Extensions(ExtendWith(MockitoExtension::class), ExtendWith(RandomBeansExtension::class))
internal class ArticleControllerTest {
	@Mock
	private lateinit var service: ArticleService

	@InjectMocks
	private lateinit var controller: ArticleController

	@Test
	internal fun shouldCreateArticle(@Random article: Article, @Random createdArticleId: ObjectId) {
		service.stub {
			on { createArticle(article) } doReturn article.copy(createdArticleId)
		}

		val response = controller.createArticle(article)

		assertThat(response.statusCode, `is`(HttpStatus.CREATED))
		assertThat(response.headers.location, `is`(URI.create("/articles/${createdArticleId}")))
	}

	@Test
	internal fun shouldGetArticle(@Random article: Article, @Random id: ObjectId) {
		val savedArticle = article.copy(id)
		service.stub { on { getArticleById(id) } doReturn savedArticle }

		val response = controller.getArticleById(id)

		assertThat(response.statusCode, `is`(HttpStatus.OK))
		assertThat(response.body, `is`(savedArticle))
	}

	@Test
	internal fun shouldGetPagedArticlesByQueryParams(
		@Random(size = 10) articles: List<Article>, @Random criteria: SearchCriteria
	) {
		val pageable = PageRequest.of(2, 3)
		service.stub { on { getArticles(criteria, pageable) } doReturn PageImpl(articles) }

		val response = controller.searchArticles(criteria, pageable)

		assertThat(response.statusCode, `is`(HttpStatus.OK))
		assertThat(response.body, `is`(PageImpl(articles)))
	}

	@Test
	internal fun shouldReturnEmptyListWhenNoArticlesFoundByQueryParams(@Random searchCriteria: SearchCriteria) {
		service.stub { on { getArticles(searchCriteria, Pageable.unpaged()) } doReturn Page.empty() }

		val response = controller.searchArticles(searchCriteria, Pageable.unpaged())

		assertThat(response.statusCode, `is`(HttpStatus.OK))
		assertThat(response.body, `is`(Page.empty()))
	}

	@Test
	internal fun shouldThrowExceptionWhenArticleNotFound(@Random id: ObjectId) {
		val thrown = assertThrows<ArticleNotFoundException> {
			controller.getArticleById(id)
		}

		assertThat(thrown.message, `is`("Could not find article with ID: $id"))
	}

	@Test
	internal fun shouldUpdateArticleConsideringIdFromPath(@Random article: Article, @Random articleId: ObjectId) {
		val response = controller.updateArticle(articleId, article)

		verify(service, times(1)).updateArticle(article.copy(id = articleId))
		assertThat(response.statusCode, `is`(HttpStatus.NO_CONTENT))
	}

	@Test
	internal fun shouldDeleteArticle(@Random id: ObjectId) {
		val response = controller.deleteArticleById(id)

		verify(service, times(1)).deleteArticleById(id)
		assertThat(response.statusCode, `is`(HttpStatus.NO_CONTENT))
	}
}
