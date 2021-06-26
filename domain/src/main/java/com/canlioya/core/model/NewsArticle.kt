package com.canlioya.core.model

import java.io.Serializable


data class NewsArticle(
    val articleId: String,
    val title: String,
    val thumbnailUrl: String?,
    val author: String?,
    val articleTrail: String?,
    val articleBody: String?,
    val date: String,
    val webUrl: String,
    val section: String,
    var isBookmarked: Boolean = false
) : Serializable