package com.canli.oya.newsreader.ui.newslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.canli.oya.newsreader.ui.main.MainScreen
import com.canlioya.core.model.NewsArticle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


const val CHOSEN_ARTICLE = "chosenArticle"

@AndroidEntryPoint
class ArticleListFragment : Fragment(), BookmarkClickListener {

    private val viewModel: NewsListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            setContent {
                val isRefreshing by viewModel.isRefreshing.collectAsState()

                MainScreen(
                    content = {
                        SwipeRefresh(
                            state = rememberSwipeRefreshState(isRefreshing),
                            onRefresh = { viewModel.refreshDataForSection() },
                        ) {
                            NewsListScreen(
                                list = viewModel.articles,
                                bookmarkClickListener = this@ArticleListFragment,
                                uiState = viewModel.uiState
                            )
                        }
                    })
            }
        }
    }

    override fun onBookmarkClick(position: Int, article: NewsArticle) {
        Timber.d("bookmark is clicked. IS BOOKMARKED: ${article.isBookmarked}")
        viewModel.toggleBookmarkState(article)
    }
}