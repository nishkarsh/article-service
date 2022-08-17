package io.github.nishkarsh.publishing.articleservice.repositories

import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import io.github.nishkarsh.publishing.articleservice.config.MongoConfig
import io.github.nishkarsh.publishing.articleservice.models.Article
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.ActiveProfiles

private const val publishDate = "publishDate"

@DataMongoTest
@ActiveProfiles("test")
@Import(MongoConfig::class)
@Extensions(ExtendWith(RandomBeansExtension::class))
internal class ArticleRepositoryTest {
	@Autowired
	private lateinit var repository: ArticleRepository

	@Autowired
	private lateinit var mongoTemplate: MongoTemplate

	@Test
	internal fun shouldCreateArticle(@Random(excludes = ["id"]) article: Article) {
		val insertedArticle = repository.insert(article)

		val foundArticles = mongoTemplate.findAll(Article::class.java)

		assertNotNull(insertedArticle)
		assertNotNull(insertedArticle.id)
		assertThat(foundArticles).hasSize(1)

		// The insertedArticle would not have the auto-converted timezone
		// since the converters would kick-in only during read
		assertThat(foundArticles[0])
			.usingRecursiveComparison()
			.ignoringFields(publishDate).isEqualTo(insertedArticle);
	}
}