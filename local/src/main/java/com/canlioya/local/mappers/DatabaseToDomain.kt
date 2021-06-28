package com.canlioya.local.mappers

import com.canlioya.core.model.NewsArticle
import com.canlioya.local.database.NewsEntity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


fun NewsEntity.toNewsArticle(): NewsArticle {
    return NewsArticle(
        this.articleId,
        this.title,
        this.thumbnailUrl,
        this.author,
        this.articleTrail,
        this.articleBody,
        this.date ?: "",
        this.webUrl,
        this.section,
        this.isBookmarked
    )
}


fun List<NewsEntity>.databaseToDomain() = this.map {
    it.toNewsArticle()
}

fun NewsArticle.toNewsEntity(): NewsEntity {
    return NewsEntity(
        this.articleId,
        this.title,
        this.thumbnailUrl,
        this.author,
        this.articleTrail,
        this.articleBody,
        this.date,
        this.webUrl,
        this.section,
        this.isBookmarked
    )
}

fun List<NewsArticle>.domainToDatabase() = this.map {
    it.toNewsEntity()
}