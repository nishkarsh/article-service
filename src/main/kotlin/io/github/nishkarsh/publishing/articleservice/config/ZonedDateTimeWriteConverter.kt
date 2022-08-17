package io.github.nishkarsh.publishing.articleservice.config

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import java.time.ZonedDateTime
import java.util.*

@WritingConverter
class ZonedDateTimeWriteConverter : Converter<ZonedDateTime, Date> {
	override fun convert(zonedDateTime: ZonedDateTime): Date {
		return Date.from(zonedDateTime.toInstant())
	}
}