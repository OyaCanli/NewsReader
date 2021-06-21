package com.canlioya.local.database

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    @Query("SELECT * FROM news WHERE section = :section ORDER BY date DESC")
    fun getArticlesForSection(section : String) : Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE articleId = :articleId LIMIT 1")
    suspend fun getChosenArticle(articleId : String) : NewsEntity

    @Query("SELECT * FROM bookmarks ORDER BY date DESC")
    fun getBookmarkedArticles() : Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(article : NewsEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(list : List<NewsEntity>)

    @Transaction
    open suspend fun bookmarkArticle(article: BookmarkEntity, isBookmarked: Boolean){
        setAsBookmark(article.articleId, true)
        if(isBookmarked){
            insertBookmark(article)
        } else {
            deleteBookmark(article.articleId)
        }
    }

    @Transaction
    open suspend fun refreshDataForSection(section: String, list : List<NewsEntity>){
        deleteArticlesFromSection(section)
        insertAll(list)
    }

    @Query("UPDATE news SET isBookmarked = :isBookmarked WHERE articleId = :articleId")
    suspend fun setAsBookmark(articleId : String, isBookmarked : Boolean)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookmark(article: BookmarkEntity)

    @Query("DELETE FROM news")
    suspend fun deleteAll()

    @Query("DELETE FROM news WHERE section = :section")
    suspend fun deleteArticlesFromSection(section: String)

    @Query("DELETE FROM bookmarks WHERE articleId = :articleId")
    suspend fun deleteBookmark(articleId : String)

    @Query("DELETE FROM news WHERE section NOT IN (:sectionList)")
    suspend fun deleteUnusedArticles(sectionList : String)

    @RawQuery
    suspend fun deleteUnusedArticles(query: SupportSQLiteQuery) : Int

}