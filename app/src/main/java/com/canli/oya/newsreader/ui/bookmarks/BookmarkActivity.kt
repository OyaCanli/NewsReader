package com.canli.oya.newsreader.ui.bookmarks

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.canli.oya.newsreader.R
import com.canli.oya.newsreader.common.CHOSEN_ARTICLE
import com.canli.oya.newsreader.ui.details.DetailsActivity
import com.canli.oya.newsreader.ui.main.MainScreen
import com.canli.oya.newsreader.ui.main.OverflowMenu
import com.canli.oya.newsreader.ui.main.SettingsDropDownItem
import com.canli.oya.newsreader.ui.main.UpButton
import com.canli.oya.newsreader.ui.newslist.ListItemClickListener
import com.canli.oya.newsreader.ui.newslist.NewsListScreen
import com.canlioya.core.model.NewsArticle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkActivity : ComponentActivity(), ListItemClickListener {

    private val viewModel: BookmarkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen(
                topAppBar = {
                    BookmarkAppBar(
                        onUpClicked = {
                            onBackPressed()
                        })
                },
                content = {
                    NewsListScreen(
                        list = viewModel.bookmarks,
                        itemClickListener = this,
                        uiState = viewModel.uiState
                    )
                })
        }
    }

    private fun toggleBookmarkState(article: NewsArticle) {
        viewModel.toggleBookmarkState(article)
    }

    private fun shareTheLink(webUrl: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, webUrl)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun openDetails(article: NewsArticle) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra(CHOSEN_ARTICLE, article)
        startActivity(intent)
    }

    override fun onListItemClick(article: NewsArticle) {
        openDetails(article)
    }

    override fun onBookmarkClick(position: Int, article: NewsArticle) {
        toggleBookmarkState(article)
    }

    override fun onShareClick(url: String) {
        shareTheLink(url)
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