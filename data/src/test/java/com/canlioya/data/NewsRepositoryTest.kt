package com.canlioya.data

import com.canlioya.core.model.NewsArticle
import com.canlioya.core.model.Result
import com.canlioya.testresources.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class NewsRepositoryTest {

    lateinit var repository: NewsRepository

    @Before
    fun setUp() {
        repository = NewsRepository(FakeLocalDataSource(), FakeRemoteDataSource(), FakeUserPreferences())
    }

    @Test
    fun getArticlesForSection_filtersCorrectly() = runBlockingTest {
        val results = repository.getArticlesForSection("politics").first()
        assertThat(results.size, `is`(1))
        assertThat(results[0].articleId, `is`(getSamplePoliticsArticle().articleId))
    }

    @Test
    fun getArticlesForSection_forUnexistingSection_returnEmptyList() = runBlockingTest {
        val results = repository.getArticlesForSection("unexistingSection").first()
        assert(results.isEmpty())
    }

    @Test
    fun getBookmarks_returnBookmarkedArticles() = runBlockingTest {
        //By default there was no bookmarks, so first bookmarking sample politics article
        val sampleArticle = getSamplePoliticsArticle()
        repository.toggleBookmarkState(sampleArticle)

        val bookmarks = repository.getBookmarks().first()
        assertThat(bookmarks.size, `is`(1))
        assertThat(bookmarks[0].articleId, `is`(sampleArticle.articleId))
    }

    @Test
    fun searchInNews_firstEmitsLoading() = runBlockingTest {
        val firstResults = repository.searchInNews("Google").first()
        assert(firstResults is Result.Loading)
    }

    @Test
    fun searchInNews_returnCorrectResult() = runBlockingTest {
        val sampleArticle = getSampleTechnologyArticle()
       val searchResults = repository.searchInNews("Google").drop(1).first()
       assert(searchResults is Result.Success)
       val resultList = (searchResults as Result.Success).data
       assert(resultList.contains(sampleArticle))
       assert(resultList.size == 1)
    }

    @Test
    fun toggleBookmarkOfExistingArticle() = runBlockingTest {
        val sampleArticle = getSampleWorldArticle()
        repository.toggleBookmarkState(sampleArticle)
        var bookmarks = repository.getBookmarks().first()
        assertThat(bookmarks.size, `is`(1))
        assertThat(bookmarks[0].articleId, `is`(sampleArticle.articleId))

        sampleArticle.isBookmarked = true
        repository.toggleBookmarkState(sampleArticle)
        bookmarks = repository.getBookmarks().first()
        assert(bookmarks.isEmpty())
    }

    @Test
    fun toggleBookmarkForANewArticle() = runBlockingTest {
        val randomArticle = NewsArticle("blabla", "blabla", "blabla", "blabla", "blabla", "blabla", "blabla", "blabla", "blabla", false )
        repository.toggleBookmarkState(randomArticle)
        var bookmarks = repository.getBookmarks().first()
        assert(bookmarks.size == 1)
        assert(bookmarks[0].articleId == randomArticle.articleId)

        repository.toggleBookmarkState(randomArticle)
        bookmarks = repository.getBookmarks().first()
        assert(!bookmarks.contains(randomArticle))
    }

}