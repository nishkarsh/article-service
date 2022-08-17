package io.github.nishkarsh.publishing.articleservice.models

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("articles")
data class Article(@Id val id: ObjectId)
