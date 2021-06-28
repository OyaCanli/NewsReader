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

    override fun getArticlesForSection(section: String) : Flow<List<NewsArticle>> {
        return database.newsDao().getArticlesForSection(section).map {
                it.databaseToDomain()
        }
    }

    override suspend fun clearUnusedData() {
        val sections = userPreferences.getSectionListPreference()
        Log.d("DBDataSource", "clearUnusedData is called. Sections are :$sections")
        val query = SimpleSQLiteQuery(buildQueryForDelete(sections))
        database.newsDao().deleteUnusedArticles(query) //todo try other method as well
    }

    private fun buildQueryForDelete(sections : List<String>) : String {
        val joinedString =  sections.joinToString(separator = ","){ "\"$it\"" }
        return "DELETE FROM news WHERE section NOT IN($joinedString) AND isBookmarked = 0;"
    }

    //////////   BOOKMARKS   ////////////////

    /**
     * Get all bookmarked articles
     *
     * @return
     */
    override fun getBookmarks() : Flow<List<NewsArticle>> {
        return database.newsDao().getBookmarkedArticles().map {
            it.databaseToDomain()
        }
    }

    /**
     * Bookmark the article: If the article is already in the database, set it as isBookmarked = true
     * If article is not yet in database (can happen if user look at search results)
     * then save the article to database with isBookmarked field as true
     * @param article
     */
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

    /**
     * Remove from bookmarks by setting isBookmarked = false
     *
     * @param articleId
     */
    override suspend fun removeFromBookmarks(articleId: String) {
        database.newsDao().removeFromBookmarks(articleId)
    }

}