package com.canlioya.local


import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.canlioya.core.model.NewsArticle
import com.canlioya.data.ILocalDataSource
import com.canlioya.data.IUserPreferences
import com.canlioya.local.database.NewsDatabase
import com.canlioya.local.mappers.databaseToDomain
import com.canlioya.local.mappers.domainToDatabase
import com.canlioya.local.mappers.toNewsArticle
import com.canlioya.local.mappers.toNewsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DBDataSource(val database: NewsDatabase, val userPreferences: IUserPreferences) : ILocalDataSource {

    override suspend fun refreshDataForSection(section : String, list: List<NewsArticle>) {
        database.newsDao().refreshDataForSection(section, list.domainToDatabase())
    }

    override suspend fun saveAsBookmark(article: NewsArticle) {
        if(articleAlreadyExists(article.articleId)){
            database.newsDao().setAsBookmark(article.articleId)
        } else {
            val bookmarkedVersion = article.copy(isBookmarked = true)
            database.newsDao().insert(bookmarkedVersion.toNewsEntity())
        }
    }

    private suspend fun articleAlreadyExists(articleId : String) : Boolean {
        val count = database.newsDao().doesArticleExistOnDB(articleId)
        return count != 0
    }

    override suspend fun removeFromBookmarks(articleId: String) {
        database.newsDao().removeFromBookmarks(articleId)
    }

    override fun getArticlesForSection(section: String) : Flow<List<NewsArticle>> {
        return database.newsDao().getArticlesForSection(section).map {
                it.databaseToDomain()
        }
    }

    override fun getBookmarks() : Flow<List<NewsArticle>> {
        return database.newsDao().getBookmarkedArticles().map {
            it.databaseToDomain()
        }
    }

    override suspend fun getArticleDetails(articleId : String) : NewsArticle {
        return database.newsDao().getChosenArticle(articleId).toNewsArticle()
    }

    override suspend fun clearUnusedData() {
        val sections = userPreferences.getSectionListPreference()
        Log.d("DBDataSource", "clearUnusedData is called. Sections are :$sections")
        val query = SimpleSQLiteQuery(buildQueryForDelete(sections))
        database.newsDao().deleteUnusedArticles(query) //todo try other method as well
    }

    override suspend fun addAsBookmark(article: NewsArticle) {
        val bookmark = article.copy(isBookmarked = true)
        database.newsDao().insert(bookmark.toNewsEntity())
    }

    private fun buildQueryForDelete(sections : List<String>) : String {
        val joinedString =  sections.joinToString(separator = ","){ "\"$it\"" }
        return "DELETE FROM news WHERE section NOT IN($joinedString) AND isBookmarked = 0;"
    }

}