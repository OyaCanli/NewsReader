package com.canlioya.core.repository

import com.canlioya.core.model.NewsArticle
import com.canlioya.core.model.Result
import kotlinx.coroutines.flow.Flow

interface INewsRepository {

    fun getArticlesForSection(section: String): Flow<List<NewsArticle>>

    fun getBookmarks(): Flow<List<NewsArticle>>

    suspend fun refreshAllData()

    suspend fun refreshDataForSection(section: String)

    suspend fun searchInNews(keyword: String) : Flow<Result<List<NewsArticle>>>

    suspend fun toggleBookmarkState(article : NewsArticle)

    suspend fun clearUnusedData()
}