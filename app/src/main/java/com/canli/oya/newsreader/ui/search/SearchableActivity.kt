package com.canli.oya.newsreader.ui.search

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import com.canli.oya.newsreader.R
import com.canli.oya.newsreader.common.*
import com.canli.oya.newsreader.ui.details.DetailsActivity
import com.canli.oya.newsreader.ui.main.*
import com.canli.oya.newsreader.ui.newslist.ListItemClickListener
import com.canli.oya.newsreader.ui.newslist.NewsListScreen
import com.canlioya.core.model.NewsArticle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchableActivity : ComponentActivity(), ListItemClickListener {

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        var query: String? = null
        if (Intent.ACTION_SEARCH == intent.action) {
            query = intent.getStringExtra(SearchManager.QUERY)
        }

        query?.let {
            viewModel.searchInNews(it)
        }

        setContent {
            MainScreen(
                topAppBar = {
                    SearchAppBar(
                        title = getString(R.string.search_results_for, query),
                        onUpClicked = {
                            onBackPressed()
                        })
                },
                content = {
                    NewsListScreen(
                        list = viewModel.results,
                        itemClickListener = this,
                        uiState = viewModel.uiState
                    )
                })
        }
    }

    override fun onListItemClick(article: NewsArticle) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra(CHOSEN_ARTICLE, article)
        startActivity(intent)
    }

    override fun onBookmarkClick(position : Int, article: NewsArticle) {
        viewModel.toggleBookmarkState(position, article)
    }

    override fun onShareClick(url: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, url)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}

@Composable
private fun SearchAppBar(title : String,
                         onUpClicked: () -> Unit) {
    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            UpButton(onUpClicked = onUpClicked)
        },
        actions = {
            OverflowMenu {
                BookmarksDropDownItem()
                SettingsDropDownItem()
            }
        }
    )
}