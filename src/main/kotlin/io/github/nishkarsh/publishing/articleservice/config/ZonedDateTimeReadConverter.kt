package io.github.nishkarsh.publishing.articleservice.config

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime
import java.util.*

@ReadingConverter
class ZonedDateTimeReadConverter : Converter<Date, ZonedDateTime> {
	override fun convert(date: Date): ZonedDateTime {
		return date.toInstant().atZone(UTC)
	}
}