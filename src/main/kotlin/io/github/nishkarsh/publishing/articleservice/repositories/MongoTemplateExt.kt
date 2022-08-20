package io.github.nishkarsh.publishing.articleservice.repositories

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.support.PageableExecutionUtils.getPage

inline fun <reified T : Any> MongoTemplate.find(query: Query, pageable: Pageable): Page<T> {
	val results = find<T>(query.with(pageable))

	return getPage(results, pageable) {
		count(Query.of(query).limit(-1).skip(-1), T::class.java)
	}
}