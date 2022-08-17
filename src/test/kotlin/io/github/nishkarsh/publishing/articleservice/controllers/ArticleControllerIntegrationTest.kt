package io.github.nishkarsh.publishing.articleservice.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import io.github.nishkarsh.publishing.articleservice.models.Article
import io.github.nishkarsh.publishing.articleservice.services.ArticleService
import org.bson.types.ObjectId
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.stub
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.ZoneId


@WebMvcTest(controllers = [ArticleController::class])
@ExtendWith(RandomBeansExtension::class)
class ArticleControllerIntegrationTest {
	@Autowired
	private lateinit var mockMvc: MockMvc

	@MockBean
	private lateinit var articleService: ArticleService

	@Autowired
	private lateinit var objectMapper: ObjectMapper

	@Test
	internal fun shouldReturnCreatedStatusWhenArticleCreated(
		@Random(excludes = ["id"]) article: Article, @Random createdId: ObjectId
	) {
		val articleLocation = "/articles/${createdId}"
		val publishDateInUTC = article.publishDate.withZoneSameInstant(ZoneId.of("UTC"))
		val deserializedArticle = article.copy(publishDate = publishDateInUTC)
		val insertedArticle = deserializedArticle.copy(id = createdId)

		articleService.stub { on { createArticle(deserializedArticle) } doReturn insertedArticle }

		val request = post("/articles/")
			.content(objectMapper.writeValueAsBytes(article))
			.contentType(MediaType.APPLICATION_JSON)
		mockMvc.perform(request)
			.andExpect(status().isCreated)
			.andExpect(header().string(HttpHeaders.LOCATION, `is`(articleLocation)))
	}
}