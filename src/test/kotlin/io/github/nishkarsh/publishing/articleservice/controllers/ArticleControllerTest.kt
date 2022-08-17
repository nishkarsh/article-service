package io.github.nishkarsh.publishing.articleservice.controllers

import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import io.github.nishkarsh.publishing.articleservice.models.Article
import io.github.nishkarsh.publishing.articleservice.services.ArticleService
import org.bson.types.ObjectId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.stub
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
			on { createArticle(article) } doReturn article.copy(id = createdArticleId)
		}

		val response = controller.createArticle(article)

		assertThat(response.statusCode, `is`(HttpStatus.CREATED))
		assertThat(response.headers.location, `is`(URI.create("/articles/${createdArticleId}")))
	}
}
