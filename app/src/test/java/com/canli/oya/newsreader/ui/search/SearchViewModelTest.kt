package com.canli.oya.newsreader.ui.search


import com.canli.oya.newsreader.common.UIState
import com.canli.oya.newsreader.data.Interactors
import com.canlioya.core.usecases.*
import com.canlioya.data.NewsRepository
import com.canlioya.testresources.FakeLocalDataSource
import com.canlioya.testresources.FakeRemoteDataSource
import com.canlioya.testresources.FakeUserPreferences
import com.canlioya.testresources.getSampleTechnologyArticle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test


@ExperimentalCoroutinesApi
class SearchViewModelTest {

    @Test
    fun searchKeyword_withResult_returnResult()  = runBlockingTest {
        val testDispatcher = TestCoroutineDispatcher()
        val repo = NewsRepository(FakeLocalDataSource(), FakeRemoteDataSource(), FakeUserPreferences())
        val interactors = Interactors(
            GetNewsForSection(repo), GetBookmarks(repo), ToggleBookmarkState(repo),
            RefreshAllData(repo), RefreshDataForSection(repo), SearchInNews(repo), CleanUnusedData(repo)
        )
        val viewModel = SearchViewModel(interactors, testDispatcher)

        viewModel.searchInNews("Google")

        val results = viewModel.results.first()
        assertThat(results.size, `is`(1))
        assertThat(results[0].articleId, `is`(getSampleTechnologyArticle().articleId))

        val uiState = viewModel.uiState.value
        assertThat(uiState, `is`(UIState.SUCCESS))
    }

    @Test
    fun searchKeyword_withoutResult_returnEmpty()  = runBlockingTest {
        val testDispatcher = TestCoroutineDispatcher()
        val repo = NewsRepository(FakeLocalDataSource(), FakeRemoteDataSource(), FakeUserPreferences())
        val interactors = Interactors(
            GetNewsForSection(repo), GetBookmarks(repo), ToggleBookmarkState(repo),
            RefreshAllData(repo), RefreshDataForSection(repo), SearchInNews(repo), CleanUnusedData(repo)
        )
        val viewModel = SearchViewModel(interactors, testDispatcher)

        viewModel.searchInNews("Xdor")

        val results = viewModel.results.first()
        assert(results.isEmpty())

        val uiState = viewModel.uiState.value
        assertThat(uiState, `is`(UIState.EMPTY))
    }
}