package com.canlioya.local.mappers

import com.canlioya.core.model.NewsArticle
import com.canlioya.local.database.BookmarkEntity


fun BookmarkEntity.toNewsArticle(): NewsArticle {
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
        true
    )
}

fun NewsArticle.toBookmarkEntity(): BookmarkEntity {
    val longDate = 0L //todo
    return BookmarkEntity(
        this.articleId,
        this.title,
        this.thumbnailUrl,
        this.author,
        this.articleTrail,
        this.articleBody,
        longDate,
        this.webUrl,
        this.section,
    )
}


fun List<BookmarkEntity>.toDomainNews() = this.map {
    it.toNewsArticle()
}