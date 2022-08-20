package io.github.nishkarsh.publishing.articleservice.models

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

internal class SearchQueryBuilderTest {
	@Test
	internal fun shouldBuildEmptyQueryWhenNoCriteria() {
		val query = SearchQueryBuilder().build()

		assertThat(query.toString(), `is`("Query: {}, Fields: {}, Sort: {}"))
	}
}