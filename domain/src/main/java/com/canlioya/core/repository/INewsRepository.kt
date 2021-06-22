package com.canlioya.core.repository

import com.canlioya.core.model.NewsArticle
import com.canlioya.core.model.Result
import kotlinx.coroutines.flow.Flow

interface INewsRepository {

    fun getArticlesForSection(section: String): Flow<Result<List<NewsArticle>>>

    fun getBookmarks(): Flow<Result<List<NewsArticle>>>

    suspend fun refreshAllData() : Flow<Result<Nothing?>>

    suspend fun refreshDataForSection(section: String) : Flow<Result<Nothing?>>

    suspend fun searchInNews(keyword: String) : Flow<Result<List<NewsArticle>>>

    suspend fun saveToBookmarks(article: NewsArticle)

    suspend fun clearUnusedData()
}