package com.canlioya.data

import com.canlioya.core.model.NewsArticle
import com.canlioya.core.model.Result
import com.canlioya.core.repository.INewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException

class NewsRepository(
    private val localDataSource: ILocalDataSource,
    private val remoteDataSource: INetworkDataSource,
    private val userPreferences: IUserPreferences
) : INewsRepository {

    override fun getArticlesForSection(section: String): Flow<List<NewsArticle>> = localDataSource.getArticlesForSection(section)

    override fun getBookmarks(): Flow<List<NewsArticle>>  {
        println("Getting bookmarks")
        return localDataSource.getBookmarks()
    }

    override suspend fun refreshAllData(): Flow<Result<Nothing?>> = flow {
        emit(Result.Loading)
        println("refreshdata is called")
        val sections = userPreferences.getSectionListPreference()
        println("sections: $sections")
        withContext(Dispatchers.IO){
            sections.forEach {
                async { refreshDataForSection(it) }
            }
        }
    }

    override suspend fun refreshDataForSection(section: String): Flow<Result<Nothing?>> = flow {
        emit(Result.Loading)
        try {
            val newArticlesForSection = remoteDataSource.getArticlesForSection(section)
            println("result list size for section $section : ${newArticlesForSection.size}")
            localDataSource.refreshDataForSection(section, newArticlesForSection)
            emit(Result.Success(null))
        } catch (e: Exception) {
            println(e)
            emit(Result.Error(e))
        }
    }

    override suspend fun searchInNews(keyword: String): Flow<Result<List<NewsArticle>>> = flow {
        emit(Result.Loading)
        try {
            val results = remoteDataSource.searchInNews(keyword)
            println("number of results for search : $results")
            emit(Result.Success(results))
        } catch (e: Exception) {
            println(e)
            emit(Result.Error(e))
        }
    }

    override suspend fun saveToBookmarks(article: NewsArticle) {
        localDataSource.saveToBookmarks(article)
    }

    override suspend fun clearUnusedData() {
        localDataSource.clearUnusedData()
    }
}