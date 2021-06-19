package com.canlioya.local


import com.canlioya.core.model.NewsArticle
import com.canlioya.data.ILocalDataSource
import com.canlioya.local.database.NewsDatabase
import com.canlioya.local.mappers.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DBDataSource(val database: NewsDatabase) : ILocalDataSource {

    override suspend fun saveFreshNews(list : List<NewsArticle>) {
        database.newsDao().deleteAll()
        database.newsDao().insertAll(list.domainToDatabase())
    }

    override suspend fun saveToBookmarks(article : NewsArticle) {
        database.newsDao().insertBookmark(article.toBookmarkEntity())
    }

    override fun getArticlesForSection(section: String) : Flow<List<NewsArticle>> {
        return database.newsDao().getArticlesForSection(section).map {
            it.databaseToDomain()
        }
    }

    override fun getBookmarks() : Flow<List<NewsArticle>> {
        return database.newsDao().getBookmarkedArticles().map {
            it.toDomainNews()
        }
    }

    override suspend fun getArticleDetails(articleId : String) : NewsArticle {
        return database.newsDao().getChosenArticle(articleId).toNewsArticle()
    }

    override suspend fun deleteArticlesFromSection(section: String) {
        database.newsDao().deleteArticlesFromSection(section)
    }
}