package com.canlioya.local.mappers

import com.canlioya.core.model.NewsArticle
import com.canlioya.local.database.NewsEntity

fun NewsEntity.toNewsArticle(): NewsArticle {
    val formattedDate = " " //todo
    return NewsArticle(
        this.articleId,
        this.title,
        this.thumbnailUrl,
        this.author,
        this.articleTrail,
        this.articleBody,
        formattedDate,
        this.webUrl,
        this.section,
        this.isBookmarked
    )
}


fun List<NewsEntity>.databaseToDomain() = this.map {
    it.toNewsArticle()
}

fun NewsArticle.toNewsEntity(): NewsEntity {
    val longDate = 0L //todo
    return NewsEntity(
        this.articleId,
        this.title,
        this.thumbnailUrl,
        this.author,
        this.articleTrail,
        this.articleBody,
        longDate,
        this.webUrl,
        this.section,
        this.isBookmarked
    )
}

fun List<NewsArticle>.domainToDatabase() = this.map {
    it.toNewsEntity()
}