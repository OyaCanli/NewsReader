package com.canlioya.data

import com.canlioya.core.model.NewsArticle
import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {

    suspend fun refreshDataForSection(section : String, list: List<NewsArticle>)

    suspend fun saveAsBookmark(article: NewsArticle)

    suspend fun removeFromBookmarks(articleId: String)

    fun getArticlesForSection(section: String): Flow<List<NewsArticle>>

    fun getBookmarks(): Flow<List<NewsArticle>>

    suspend fun getArticleDetails(articleId: String): NewsArticle

    suspend fun clearUnusedData()

    suspend fun addAsBookmark(article: NewsArticle)
}