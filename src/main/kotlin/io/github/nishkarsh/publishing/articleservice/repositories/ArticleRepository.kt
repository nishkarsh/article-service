package io.github.nishkarsh.publishing.articleservice.repositories

import io.github.nishkarsh.publishing.articleservice.models.Article
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository : MongoRepository<Article, ObjectId>
