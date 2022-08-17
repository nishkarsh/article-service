package io.github.nishkarsh.publishing.articleservice.repositories

import io.github.nishkarsh.publishing.articleservice.models.Article
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository : MongoRepository<Article, Id>
