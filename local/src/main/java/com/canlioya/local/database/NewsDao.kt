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

    @Query("SELECT * FROM news WHERE isBookmarked = 1 ORDER BY date DESC")
    fun getBookmarkedArticles() : Flow<List<NewsEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(article : NewsEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(list : List<NewsEntity>)

    @Transaction
    open suspend fun refreshDataForSection(section: String, list : List<NewsEntity>){
        deleteArticlesFromSection(section)
        insertAll(list)
    }

    @Query("UPDATE news SET isBookmarked = 1 WHERE articleId = :articleId")
    suspend fun setAsBookmark(articleId : String)

    @Query("UPDATE news SET isBookmarked = 0 WHERE articleId = :articleId")
    suspend fun removeFromBookmarks(articleId : String)

    @Query("SELECT COUNT(1) FROM news WHERE articleId= :articleId")
    suspend fun doesArticleExistOnDB(articleId : String) : Int

    @Query("DELETE FROM news WHERE section = :section AND isBookmarked = 0")
    suspend fun deleteArticlesFromSection(section: String)

    @Query("DELETE FROM news WHERE section NOT IN (:sectionList) AND isBookmarked = 0")
    suspend fun deleteUnusedArticles(sectionList : String)

    @RawQuery
    suspend fun deleteUnusedArticles(query: SupportSQLiteQuery) : Int

}