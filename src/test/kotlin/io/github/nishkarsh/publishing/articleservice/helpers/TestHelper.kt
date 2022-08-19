package io.github.nishkarsh.publishing.articleservice.helpers

import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

object TestHelper {
	fun toUtcAndTruncatedToSeconds(date: ZonedDateTime): ZonedDateTime {
		return date.withZoneSameInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
	}

	fun truncateToSeconds(date: ZonedDateTime): ZonedDateTime {
		return date.truncatedTo(ChronoUnit.SECONDS)
	}
}