package com.canli.oya.newsreader.ui.bookmarks

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import com.canli.oya.newsreader.R
import com.canli.oya.newsreader.common.*
import com.canli.oya.newsreader.databinding.ActivityListBinding
import com.canli.oya.newsreader.ui.details.DetailsActivity
import com.canli.oya.newsreader.ui.main.MainActivity
import com.canli.oya.newsreader.ui.main.MainScreen
import com.canli.oya.newsreader.ui.newslist.ArticleAdapter
import com.canli.oya.newsreader.ui.newslist.ListItemClickListener
import com.canli.oya.newsreader.ui.newslist.NewsListScreen
import com.canli.oya.newsreader.ui.settings.SettingsActivity
import com.canlioya.core.model.NewsArticle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class BookmarkActivity : ComponentActivity(), ListItemClickListener {

    private val viewModel: BookmarkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen(
                topAppBar = {
                    BookmarkAppBar(
                        onSettingsClicked = { launchSettings() },
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

    private fun launchSettings() {
        val intent = Intent(this@BookmarkActivity, SettingsActivity::class.java)
        startActivity(intent)
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