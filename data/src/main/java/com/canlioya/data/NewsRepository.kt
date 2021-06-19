package com.canlioya.data

import com.canlioya.core.model.NewsArticle
import com.canlioya.core.repository.INewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class NewsRepository(
    private val localDataSource: ILocalDataSource,
    private val remoteDataSource: INetworkDataSource,
    private val userPreferences: IUserPreferences
) : INewsRepository {

    override fun getArticlesForSection(section: String): Flow<List<NewsArticle>> {
        println("Getting articles for section")
        return localDataSource.getArticlesForSection(section)
    }

    override fun getBookmarks(): Flow<List<NewsArticle>> {
        return localDataSource.getBookmarks()
    }

    override suspend fun refreshData(){
        println("refreshdata is called")
        val sections = userPreferences.getSectionListPreference()
        withContext(Dispatchers.IO){
            sections.forEach {
                async { refreshDataForSection(it) }
            }
        }
    }

    override suspend fun refreshDataForSection(section: String) {
        val newArticlesForSection = remoteDataSource.getArticlesForSection(section)
        println("result list size: ${newArticlesForSection.size}")
        localDataSource.deleteArticlesFromSection(section)
        localDataSource.saveFreshNews(newArticlesForSection)
    }

    override suspend fun searchInNews(keyword: String) {
        remoteDataSource.searchInNews(keyword)
    }

    override suspend fun saveToBookmarks(article: NewsArticle) {
        localDataSource.saveToBookmarks(article)
    }
}