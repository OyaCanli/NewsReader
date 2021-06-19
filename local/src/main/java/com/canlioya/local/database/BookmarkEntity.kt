package com.canlioya.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity (@PrimaryKey val articleId : String,
                       val title : String,
                       val thumbnailUrl : String?,
                       val author : String?,
                       val articleTrail : String?,
                       val articleBody : String?,
                       val date : Long,
                       val webUrl : String,
                       val section : String)