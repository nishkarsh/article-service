package io.github.nishkarsh.publishing.articleservice.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import io.github.nishkarsh.publishing.articleservice.helpers.TestHelper.getSampleArticlesWithPublishDates
import io.github.nishkarsh.publishing.articleservice.helpers.TestHelper.toUtcAndTruncatedToSeconds
import io.github.nishkarsh.publishing.articleservice.models.Article
import org.bson.types.ObjectId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.TestInstance.Lifecycle
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.ZonedDateTime


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

	@Nested
	@TestInstance(Lifecycle.PER_CLASS)
	inner class ArticlesSearchIntegrationTest {
		private lateinit var savedArticles: Collection<Article>

		@BeforeEach
		internal fun setUp() {
			val juneOne2022 = ZonedDateTime.parse("2022-06-01T10:10:20.200Z")
			val julyTwo2022 = ZonedDateTime.parse("2022-07-02T10:10:20.200Z")

			val articles = getSampleArticlesWithPublishDates(juneOne2022, julyTwo2022)
			savedArticles = mongoTemplate.insertAll(articles)
		}

		@Test
		internal fun shouldSearchArticlesByAuthorName() {
			val request = get("/articles").param("author", "nish")

			mockMvc.perform(request)
				.andExpect(status().isOk)
				.andExpect(jsonPath("$.content[0].header", `is`("Article One")))
				.andExpect(jsonPath("$.content[1].header", `is`("Article Two")))
				.andExpect(jsonPath("$.totalPages", `is`(1)))
				.andExpect(jsonPath("$.totalElements", `is`(2)))
		}

		@Test
		internal fun shouldSearchArticlesByKeyword() {
			val request = get("/articles").param("keyword", "july")

			mockMvc.perform(request)
				.andExpect(status().isOk)
				.andExpect(jsonPath("$.content[0].header", `is`("Article Two")))
				.andExpect(jsonPath("$.content[1].header", `is`("Article Three")))
				.andExpect(jsonPath("$.totalPages", `is`(1)))
				.andExpect(jsonPath("$.totalElements", `is`(2)))
		}

		@Test
		internal fun shouldSearchArticlesByPublishDate() {
			val juneOne2022 = ZonedDateTime.parse("2022-06-01T10:10:20.200Z")
			val julyOne2022 = ZonedDateTime.parse("2022-07-01T10:10:20.200Z")

			val request = get("/articles")
				.param("fromPublishDate", juneOne2022.toString())
				.param("toPublishDate", julyOne2022.toString())

			mockMvc.perform(request)
				.andExpect(status().isOk)
				.andExpect(jsonPath("$.content[0].header", `is`("Article One")))
				.andExpect(jsonPath("$.totalPages", `is`(1)))
				.andExpect(jsonPath("$.totalElements", `is`(1)))
		}

		@Test
		internal fun shouldSearchArticlesWithMultipleCriteria() {
			val request = get("/articles")
				.param("author", "Nishkarsh")
				.param("keyword", "july")

			mockMvc.perform(request)
				.andExpect(status().isOk)
				.andExpect(jsonPath("$.content[0].header", `is`("Article Two")))
				.andExpect(jsonPath("$.totalPages", `is`(1)))
				.andExpect(jsonPath("$.totalElements", `is`(1)))
		}

		@Test
		internal fun shouldGetAllArticlesPagedWhenNoQuerySpecified(
			@Random(size = 20, type = Article::class) moreArticles: List<Article>
		) {
			mongoTemplate.insertAll(moreArticles).also {
				assertThat(it, hasSize(20))
			}

			mockMvc.perform(get("/articles"))
				.andExpect(status().isOk)
				.andExpect(jsonPath("$.totalPages", `is`(2)))
				.andExpect(jsonPath("$.numberOfElements", `is`(20)))
				.andExpect(jsonPath("$.totalElements", `is`(23)))
		}

		@AfterEach
		internal fun tearDown() {
			mongoTemplate.dropCollection(Article::class.java)
		}
	}
}