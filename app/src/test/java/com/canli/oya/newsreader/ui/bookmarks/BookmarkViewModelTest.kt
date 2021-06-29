package com.canli.oya.newsreader.ui.bookmarks

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import com.canli.oya.newsreader.common.UIState
import com.canli.oya.newsreader.data.Interactors
import com.canlioya.core.usecases.*
import com.canlioya.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class BookmarkViewModelTest {

    private var testDispatcher = TestCoroutineDispatcher()

    private var repo = NewsRepository(FakeLocalDataSource(), FakeRemoteDataSource(), FakeUserPreferences())

    private var interactors = Interactors(
        GetNewsForSection(repo), GetBookmarks(repo), ToggleBookmarkState(repo),
        RefreshAllData(repo), RefreshDataForSection(repo), SearchInNews(repo), CleanUnusedData(repo)
    )

    val viewModel = BookmarkViewModel(interactors, testDispatcher)

    @Test
    fun startCollectingBookmarks_noBookmarks_returnEmpty() = runBlockingTest{
        viewModel.startCollectingBookmarks()
        val bookmarks = viewModel.bookmarks.first()
        assert(bookmarks.isEmpty())
        val uiState = viewModel.uiState.value
        assert(uiState == UIState.EMPTY)
    }

    @Test
    fun startCollectingBookmarks_withBookmarks_returnEmpty() = runBlockingTest{
        interactors.toggleBookmarkState(sampleWorldArticle)
        viewModel.startCollectingBookmarks()
        val bookmarks = viewModel.bookmarks.first()
        assert(bookmarks.contains(sampleWorldArticle))
        val uiState = viewModel.uiState.value
        assert(uiState == UIState.SUCCESS)
    }
}