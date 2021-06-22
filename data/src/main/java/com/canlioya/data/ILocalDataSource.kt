package com.canlioya.data

import com.canlioya.core.model.NewsArticle
import com.canlioya.core.model.Result
import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {

    suspend fun refreshDataForSection(section : String, list: List<NewsArticle>)

    suspend fun saveToBookmarks(article: NewsArticle)

    fun getArticlesForSection(section: String): Flow<List<NewsArticle>>

    fun getBookmarks(): Flow<List<NewsArticle>>

    suspend fun getArticleDetails(articleId: String): NewsArticle

    suspend fun clearUnusedData()
}