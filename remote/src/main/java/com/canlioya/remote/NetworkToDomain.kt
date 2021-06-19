package com.canlioya.remote

import com.canlioya.core.model.NewsArticle
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


fun ArticleDTO.toNewsArticle(): NewsArticle {
    return NewsArticle(
        this.id,
        this.webTitle,
        this.fields?.thumbnail,
        this.fields?.byline,
        this.fields?.trailText,
        this.fields?.body,
        this.webPublicationDate,
        this.webUrl,
        this.sectionId,
        false
    )
}

fun List<ArticleDTO>.toDomainNews() = this.map {
    it.toNewsArticle()
}