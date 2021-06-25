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
        formatDateAndTime(this.webPublicationDate),
        this.webUrl,
        this.sectionId,
        false
    )
}

fun List<ArticleDTO>.toDomainNews() = this.map {
    it.toNewsArticle()
}

fun formatDateAndTime(unformattedTime : String) : String {
    val timeZone = TimeZone.getTimeZone("UTC")
    val sourceFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    sourceFormat.timeZone = timeZone
    var parsedTime: Date? = null
    try {
        parsedTime = sourceFormat.parse(unformattedTime)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    val usersTimeZone = TimeZone.getDefault()
    val destFormat = SimpleDateFormat("LLL dd, yyyy'T'HH:mm");
    destFormat.timeZone = usersTimeZone
    return destFormat.format(parsedTime)

}