package io.github.nishkarsh.publishing.articleservice.repositories

import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import io.github.nishkarsh.publishing.articleservice.config.MongoConfig
import io.github.nishkarsh.publishing.articleservice.models.Article
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.ActiveProfiles

@DataMongoTest
@ActiveProfiles("test")
@Import(MongoConfig::class)
@Extensions(ExtendWith(RandomBeansExtension::class))
internal class MongoTemplateExtKtTest {
	@Autowired
	lateinit var mongoTemplate: MongoTemplate

	@BeforeEach
	internal fun setUp() {
		mongoTemplate.dropCollection(Article::class.java)
	}

	@Test
	internal fun shouldReturnPagedResults(@Random(size = 10, type = Article::class) articles: List<Article>) {
		mongoTemplate.insertAll(articles)

		val pagedResults = mongoTemplate.find<Article>(Query.query(Criteria()), Pageable.ofSize(2))

		assertThat(pagedResults.totalPages, `is`(5))
		assertThat(pagedResults.totalElements, `is`(10))
		assertThat(pagedResults.content, hasSize(2))
	}

	@AfterEach
	internal fun tearDown() {
		mongoTemplate.dropCollection(Article::class.java)
	}
}