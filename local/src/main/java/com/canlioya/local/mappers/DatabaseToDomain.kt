package com.canlioya.local.mappers

import com.canlioya.core.model.NewsArticle
import com.canlioya.local.database.NewsEntity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


fun NewsEntity.toNewsArticle(): NewsArticle {
    val formattedDate = convertLongToFormattedDate(this.date)
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
    val longDate = convertStringDateToLong(this.date)
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

fun convertStringDateToLong(date : String) : Long? {
    val timeZone = TimeZone.getTimeZone("UTC")
    val sourceFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    sourceFormat.timeZone = timeZone
    var parsedTime: Date? = null
    try {
        parsedTime = sourceFormat.parse(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return parsedTime?.time
}

fun convertLongToFormattedDate(time : Long?) : String {
    if(time ==null) {
        return ""
    }

    val date = Date(time)

    val tz = TimeZone.getDefault()
    val destFormat = SimpleDateFormat("LLL dd, yyyy'T'HH:mm")
    destFormat.timeZone = tz
    return destFormat.format(date)
}