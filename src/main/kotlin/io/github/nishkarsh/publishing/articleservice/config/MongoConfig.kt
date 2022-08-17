package io.github.nishkarsh.publishing.articleservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.convert.MongoCustomConversions


@Configuration
internal class MongoConfig() {
	@Bean
	fun customConversions(): MongoCustomConversions {
		return MongoCustomConversions(listOf(ZonedDateTimeWriteConverter(), ZonedDateTimeReadConverter()))
	}
}