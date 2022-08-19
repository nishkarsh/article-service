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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
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

		mockMvc.perform(MockMvcRequestBuilders.get("/articles/{id}", insertedArticle.id))
			.andExpect(status().isOk)
			.andExpect(content().json(objectMapper.writeValueAsString(insertedArticle)))
	}

	@Test
	internal fun shouldReturn404WhenArticleNotFound(@Random articleId: ObjectId) {
		mockMvc.perform(MockMvcRequestBuilders.get("/articles/{id}", articleId))
			.andExpect(status().isNotFound)
			.andExpect(content().json("{\"message\":\"Could not find article with ID: ${articleId}\"}"))
	}
}