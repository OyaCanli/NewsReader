package com.canli.oya.newsreader.ui


import com.canli.oya.newsreader.common.UIState
import com.canli.oya.newsreader.data.Interactors
import com.canli.oya.newsreader.ui.bookmarks.BookmarkViewModel
import com.canlioya.core.usecases.*
import com.canlioya.data.*
import com.canlioya.testresources.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test

@ExperimentalCoroutinesApi
class BookmarkViewModelTest {

    @Test
    fun startCollectingBookmarks_noBookmarks_returnEmpty() = runBlockingTest{
        val testDispatcher = TestCoroutineDispatcher()
        val repo = NewsRepository(FakeLocalDataSource(), FakeRemoteDataSource(), FakeUserPreferences())
        val interactors = Interactors(
            GetNewsForSection(repo), GetBookmarks(repo), ToggleBookmarkState(repo),
            RefreshAllData(repo), RefreshDataForSection(repo), SearchInNews(repo), CleanUnusedData(repo)
        )
        val viewModel = BookmarkViewModel(interactors, testDispatcher)
        val bookmarks = viewModel.bookmarks.first()
        assert(bookmarks.isEmpty())
        val uiState = viewModel.uiState.value
        assertThat(uiState, `is`(UIState.EMPTY))
    }

    @Test
    fun startCollectingBookmarks_withBookmarks_returnBookmarks() = runBlockingTest{
        val testDispatcher = TestCoroutineDispatcher()
        val fakeLocalDataSource = FakeLocalDataSource(
            mutableListOf(getSampleWorldArticle().copy(isBookmarked = true),
        getSampleTechnologyArticle(), getSamplePoliticsArticle()))
        val repo = NewsRepository(fakeLocalDataSource, FakeRemoteDataSource(), FakeUserPreferences())
        val interactors = Interactors(
            GetNewsForSection(repo), GetBookmarks(repo), ToggleBookmarkState(repo),
            RefreshAllData(repo), RefreshDataForSection(repo), SearchInNews(repo), CleanUnusedData(repo)
        )
        val viewModel = BookmarkViewModel(interactors, testDispatcher)
        val sampleArticle = getSampleWorldArticle()
        viewModel.startCollectingBookmarks()
        val bookmarks = viewModel.bookmarks.first()
        assertThat(bookmarks[0].articleId, `is`(sampleArticle.articleId))
        val uiState = viewModel.uiState.value
        assertThat(uiState, `is`(UIState.SUCCESS))
    }
}