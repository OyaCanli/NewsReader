package com.canli.oya.newsreader.ui.bookmarks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.canli.oya.newsreader.R
import com.canli.oya.newsreader.ui.main.MainScreen
import com.canli.oya.newsreader.ui.main.OverflowMenu
import com.canli.oya.newsreader.ui.main.SettingsDropDownItem
import com.canli.oya.newsreader.ui.main.UpButton
import com.canli.oya.newsreader.ui.newslist.BookmarkClickListener
import com.canli.oya.newsreader.ui.newslist.NewsListScreen
import com.canlioya.core.model.NewsArticle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkActivity : ComponentActivity(), BookmarkClickListener {

    private val viewModel: BookmarkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen(
                topAppBar = {
                    BookmarkAppBar(
                        onUpClicked = {
                            onBackPressed()
                        }
                    )
                },
                content = {
                    NewsListScreen(
                        list = viewModel.bookmarks,
                        bookmarkClickListener = this,
                        uiState = viewModel.uiState
                    )
                }
            )
        }
    }

    private fun toggleBookmarkState(article: NewsArticle) {
        viewModel.toggleBookmarkState(article)
    }

    override fun onBookmarkClick(position: Int, article: NewsArticle) {
        toggleBookmarkState(article)
    }
}

@Composable
private fun BookmarkAppBar(onUpClicked: () -> Unit) {
    TopAppBar(
        title = {
            Text(text = stringResource(R.string.bookmark))
        },
        navigationIcon = {
            UpButton(onUpClicked = onUpClicked)
        },
        actions = {
            OverflowMenu {
                SettingsDropDownItem()
            }
        }
    )
}
