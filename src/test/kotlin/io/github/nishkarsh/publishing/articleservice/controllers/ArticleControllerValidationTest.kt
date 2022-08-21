package io.github.nishkarsh.publishing.articleservice.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import io.github.nishkarsh.publishing.articleservice.helpers.TestHelper.withValidTextFields
import io.github.nishkarsh.publishing.articleservice.models.Article
import io.github.nishkarsh.publishing.articleservice.services.ArticleService
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [ArticleController::class])
@ExtendWith(RandomBeansExtension::class)
class ArticleControllerValidationTest {
	@MockBean
	private lateinit var service: ArticleService

	@Autowired
	private lateinit var objectMapper: ObjectMapper

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Test
	internal fun shouldReturn400WhenHeaderEmpty(@Random article: Article) {
		val request = post("/articles/")
			.content(objectMapper.writeValueAsBytes(article.withValidTextFields().copy(header = "")))
			.contentType(APPLICATION_JSON)

		mockMvc.perform(request)
			.andExpect(status().isBadRequest)
			.andExpect(jsonPath("$.errors", hasSize<List<String>>(2)))
			.andExpect(jsonPath("$.message", `is`("An error occurred while validating request body")))
			.andExpect(jsonPath("$.errors", hasItem("header: must not be empty")))
			.andExpect(jsonPath("$.errors", hasItem("header: size must be between 10 and 100")))

	}

	@Test
	internal fun shouldReturn400WhenHeaderIsMoreThan100Chars(@Random article: Article) {
		val articleWithLongHeader = article.withValidTextFields().copy(
			header = "This is a really long header, if you don't believe then you must keep reading " +
					"this and then you will know that this is a really long and boring header"
		)
		val request = post("/articles/")
			.content(objectMapper.writeValueAsBytes(articleWithLongHeader))
			.contentType(APPLICATION_JSON)

		mockMvc.perform(request)
			.andExpect(status().isBadRequest)
			.andExpect(jsonPath("$.errors", hasSize<List<String>>(1)))
			.andExpect(jsonPath("$.errors[0]", `is`("header: size must be between 10 and 100")))
	}
}