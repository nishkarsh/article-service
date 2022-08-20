package io.github.nishkarsh.publishing.articleservice.services

import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import io.github.nishkarsh.publishing.articleservice.exceptions.ArticleNotFoundException
import io.github.nishkarsh.publishing.articleservice.models.Article
import io.github.nishkarsh.publishing.articleservice.models.SearchCriteria
import io.github.nishkarsh.publishing.articleservice.models.SearchQueryBuilder
import io.github.nishkarsh.publishing.articleservice.repositories.ArticleRepository
import org.bson.types.ObjectId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.Assertions.assertNull
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
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import java.util.*

@Extensions(ExtendWith(MockitoExtension::class), ExtendWith(RandomBeansExtension::class))
internal class ArticleServiceTest {
	@Mock
	private lateinit var repository: ArticleRepository

	@Mock
	private lateinit var mongoTemplate: MongoTemplate

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

	@Test
	internal fun shouldGetPagedArticlesBasedOnSearchCriteria(
		@Random criteria: SearchCriteria, @Random(size = 3) articles: List<Article>
	) {
		val pageable = Pageable.ofSize(3)
		val query = with(criteria) {
			SearchQueryBuilder().authorNameLike(author).fromPublishDate(fromPublishDate)
				.toPublishDate(toPublishDate).keywordLike(keyword).build()
		}

		mongoTemplate.stub {
			on { find(query.with(pageable), Article::class.java) } doReturn articles
			on { count(Query.of(query).limit(-1).skip(-1), Article::class.java) } doReturn 10
		}

		val pagedArticles = service.getArticles(criteria, pageable)

		assertThat(pagedArticles.numberOfElements, `is`(3))
		assertThat(pagedArticles.totalPages, `is`(4))
		assertThat(pagedArticles.totalElements, `is`(10))
		assertThat(pagedArticles.content, `is`(articles))
	}

	@Test
	internal fun shouldThrowExceptionOnUpdateWhenArticleNotFound(@Random article: Article) {
		repository.stub { on { existsById(article.id!!) } doReturn false }

		val exception = assertThrows<ArticleNotFoundException> {
			service.updateArticle(article)
		}

		assertThat(exception.message, `is`("Could not find article with ID: ${article.id}"))
	}

	@Test
	internal fun shouldUpdateArticle(@Random article: Article, @Random updatedArticle: Article) {
		repository.stub {
			on { existsById(article.id!!) } doReturn true
			on { save(article) } doReturn updatedArticle
		}

		val returnedArticle = service.updateArticle(article)

		verify(repository, times(1)).save(article)
		assertThat(returnedArticle, `is`(updatedArticle))
	}

	@Test
	internal fun shouldThrowExceptionOnDeleteWhenArticleNotFound(@Random articleId: ObjectId) {
		repository.stub { on { existsById(articleId) } doReturn false }

		val exception = assertThrows<ArticleNotFoundException> {
			service.deleteArticleById(articleId)
		}

		assertThat(exception.message, `is`("Could not find article with ID: $articleId"))
	}

	@Test
	internal fun shouldDeleteArticleByID(@Random articleId: ObjectId) {
		repository.stub { on { existsById(articleId) } doReturn true }

		service.deleteArticleById(articleId)

		verify(repository, times(1)).deleteById(articleId)
	}
}