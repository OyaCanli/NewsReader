package com.canlioya.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey val articleId: String,
    val title: String,
    val thumbnailUrl: String?,
    val author: String?,
    val articleTrail: String?,
    val articleBody: String?,
    val date: Long?,
    val webUrl: String,
    val section: String,
    val isBookmarked: Boolean
)