package com.canlioya.core.repository

import com.canlioya.core.model.NewsArticle
import kotlinx.coroutines.flow.Flow

interface INewsRepository {

    fun getArticlesForSection(section: String): Flow<List<NewsArticle>>

    fun getBookmarks(): Flow<List<NewsArticle>>

    suspend fun refreshData()

    suspend fun refreshDataForSection(section: String)

    suspend fun searchInNews(keyword: String)

    suspend fun saveToBookmarks(article: NewsArticle)

    suspend fun clearUnusedData()
}