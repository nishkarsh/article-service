package io.github.nishkarsh.publishing.articleservice.services

import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import io.github.nishkarsh.publishing.articleservice.models.Article
import io.github.nishkarsh.publishing.articleservice.repositories.ArticleRepository
import org.bson.types.ObjectId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.stub
import java.util.*

@Extensions(ExtendWith(MockitoExtension::class), ExtendWith(RandomBeansExtension::class))
internal class ArticleServiceTest {
	@Mock
	private lateinit var repository: ArticleRepository

	@InjectMocks
	private lateinit var service: ArticleService

	@Test
	internal fun shouldCreateArticle(@Random article: Article, @Random createdArticle: Article) {
		repository.stub {
			on { insert(article) } doReturn createdArticle
		}

		val returnedArticle = service.createArticle(article)

		assertThat(returnedArticle, `is`(createdArticle))
	}

	@Test
	internal fun shouldGetArticleById(@Random article: Article) {
		repository.stub {
			on { findById(article.id!!) } doReturn Optional.of(article)
		}

		val returnedArticle = service.getArticleById(article.id!!)

		assertThat(returnedArticle, `is`(article))
	}

	@Test
	internal fun shouldReturnNullIfArticleNotFound(@Random articleId: ObjectId) {
		repository.stub {
			on { findById(articleId) } doReturn Optional.empty()
		}

		val returnedArticle = service.getArticleById(articleId)

		assertNull(returnedArticle)
	}
}