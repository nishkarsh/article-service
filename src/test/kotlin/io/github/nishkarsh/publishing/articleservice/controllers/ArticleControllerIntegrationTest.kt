package io.github.nishkarsh.publishing.articleservice.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import io.github.nishkarsh.publishing.articleservice.helpers.TestHelper.toUtcAndTruncatedToSeconds
import io.github.nishkarsh.publishing.articleservice.models.Article
import org.bson.types.ObjectId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findById
import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(RandomBeansExtension::class)
class ArticleControllerIntegrationTest {
	@Autowired
	private lateinit var mockMvc: MockMvc

	@Autowired
	private lateinit var objectMapper: ObjectMapper

	@Autowired
	private lateinit var mongoTemplate: MongoTemplate

	@BeforeEach
	fun setUp() {
		mongoTemplate.dropCollection(Article::class.java)
	}

	@Test
	internal fun shouldCreateArticle(@Random(excludes = ["id"]) article: Article) {
		val request = post("/articles/")
			.content(objectMapper.writeValueAsBytes(article))
			.contentType(APPLICATION_JSON)

		val result = mockMvc.perform(request)
			.andExpect(status().isCreated).andReturn()

		val foundArticles = mongoTemplate.findAll(Article::class.java)
		assertThat(foundArticles, hasSize(1))
		assertThat(result.response.getHeaderValue(LOCATION), `is`("/articles/${foundArticles[0].id}"))
	}

	@Test
	internal fun shouldFindArticleById(@Random(excludes = ["id"]) article: Article) {
		val articleToInsert = article.copy(publishDate = toUtcAndTruncatedToSeconds(article.publishDate))
		val insertedArticle = mongoTemplate.insert<Article>(articleToInsert)

		mockMvc.perform(get("/articles/{id}", insertedArticle.id))
			.andExpect(status().isOk)
			.andExpect(content().json(objectMapper.writeValueAsString(insertedArticle)))
	}

	@Test
	internal fun shouldReturn404WhenArticleNotFound(@Random articleId: ObjectId) {
		mockMvc.perform(get("/articles/{id}", articleId))
			.andExpect(status().isNotFound)
			.andExpect(content().json("{\"message\":\"Could not find article with ID: ${articleId}\"}"))
	}

	@Test
	internal fun shouldUpdateArticle(@Random article: Article) {
		val articleToInsert = article.copy(publishDate = toUtcAndTruncatedToSeconds(article.publishDate))
		val insertedArticle = mongoTemplate.insert<Article>(articleToInsert)
		assertNotNull(insertedArticle)

		val articleToUpdate = insertedArticle.copy(header = "This article has been updated")
		val request = put("/articles/{id}", insertedArticle.id)
			.content(objectMapper.writeValueAsBytes(articleToUpdate))
			.contentType(APPLICATION_JSON)

		mockMvc.perform(request).andExpect(status().isNoContent)

		val savedArticle = mongoTemplate.findById<Article>(insertedArticle.id!!)
		assertThat(savedArticle, `is`(articleToUpdate))
	}

	@Test
	internal fun shouldNotAllowCreateUsingPutMethod(@Random article: Article) {
		val request = put("/articles/{id}", article.id)
			.content(objectMapper.writeValueAsBytes(article))
			.contentType(APPLICATION_JSON)

		mockMvc.perform(request)
			.andExpect(status().isNotFound)
			.andExpect(content().json("{\"message\":\"Could not find article with ID: ${article.id}\"}"))
	}

	@Test
	internal fun shouldDeleteArticle(@Random article: Article) {
		val insertedArticle = mongoTemplate.insert<Article>(article)
		assertNotNull(insertedArticle)

		mockMvc.perform(delete("/articles/{id}", article.id))
			.andExpect(status().isNoContent)

		val foundArticle = mongoTemplate.findById<Article>(article.id!!)
		assertNull(foundArticle)
	}

	@Test
	internal fun shouldReturn404WhenArticleNotFoundForDeletion(@Random articleId: ObjectId) {
		mockMvc.perform(delete("/articles/{id}", articleId))
			.andExpect(status().isNotFound)
			.andExpect(content().json("{\"message\":\"Could not find article with ID: ${articleId}\"}"))
	}
}