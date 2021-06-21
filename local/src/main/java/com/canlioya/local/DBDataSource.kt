package com.canlioya.local


import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.canlioya.core.model.NewsArticle
import com.canlioya.data.ILocalDataSource
import com.canlioya.data.IUserPreferences
import com.canlioya.local.database.NewsDatabase
import com.canlioya.local.mappers.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DBDataSource(val database: NewsDatabase, val userPreferences: IUserPreferences) : ILocalDataSource {

    override suspend fun refreshDataForSection(section : String, list: List<NewsArticle>) {
        database.newsDao().refreshDataForSection(section, list.domainToDatabase())
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

    override suspend fun clearUnusedData() {
        val sections = userPreferences.getSectionListPreference()
        Log.d("DBDataSource", "clearUnusedData is called. Sections are :$sections")
        val query = SimpleSQLiteQuery(buildQueryForDelete(sections))
        database.newsDao().deleteUnusedArticles(query)
    }

    private fun buildQueryForDelete(sections : List<String>) : String {
        val joinedString =  sections.joinToString(separator = ","){ "\"$it\"" }
        return "DELETE FROM news WHERE section NOT IN($joinedString);"
    }

    private fun testMethod() : String {
        return "DELETE FROM news WHERE section IN(\"business\", \"science\", \"technology\");"
    }


}