package com.canli.oya.newsreader.ui

import androidx.lifecycle.SavedStateHandle
import com.canli.oya.newsreader.data.Interactors
import com.canli.oya.newsreader.ui.main.SECTION_KEY
import com.canli.oya.newsreader.ui.newslist.ArticleListViewModel
import com.canlioya.core.usecases.*
import com.canlioya.data.NewsRepository
import com.canlioya.testresources.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class ArticleListViewModelTest {

    @Test
    fun launchedForSectionPolitics_collectCorrectArticles() = runBlockingTest{
        val testDispatcher = TestCoroutineDispatcher()
        val repo = NewsRepository(FakeLocalDataSource(), FakeRemoteDataSource(), FakeUserPreferences())
        val interactors = Interactors(
            GetNewsForSection(repo), GetBookmarks(repo), ToggleBookmarkState(repo),
            RefreshAllData(repo), RefreshDataForSection(repo), SearchInNews(repo), CleanUnusedData(repo)
        )
        val args = mutableMapOf<String, Any>(SECTION_KEY to "politics")
        val dummySavedStateHandler = SavedStateHandle(args)
        val viewModel = ArticleListViewModel(interactors, dummySavedStateHandler, testDispatcher)

        val articles = viewModel.articles.first()
        assertThat(articles.size, `is`(1))
        assertThat(articles[0].articleId,`is`(getSamplePoliticsArticle().articleId))
    }

    @Test
    fun launchedForSectionTechnology_collectCorrectArticles() = runBlockingTest{
        val testDispatcher = TestCoroutineDispatcher()
        val repo = NewsRepository(FakeLocalDataSource(), FakeRemoteDataSource(), FakeUserPreferences())
        val interactors = Interactors(
            GetNewsForSection(repo), GetBookmarks(repo), ToggleBookmarkState(repo),
            RefreshAllData(repo), RefreshDataForSection(repo), SearchInNews(repo), CleanUnusedData(repo)
        )
        val args = mutableMapOf<String, Any>(SECTION_KEY to "technology")
        val dummySavedStateHandler = SavedStateHandle(args)
        val viewModel = ArticleListViewModel(interactors, dummySavedStateHandler, testDispatcher)

        val articles = viewModel.articles.first()
        assertThat(articles.size, `is`(1))
        assertThat(articles[0].articleId,`is`(getSampleTechnologyArticle().articleId))
    }

}