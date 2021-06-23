package com.example.oya.newsreader.ui.bookmarks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import com.canlioya.core.model.NewsArticle
import com.example.oya.newsreader.R
import com.example.oya.newsreader.common.UIState
import com.example.oya.newsreader.common.showList
import com.example.oya.newsreader.common.showLoading
import com.example.oya.newsreader.databinding.ActivityListBinding
import com.example.oya.newsreader.ui.details.DetailsActivity
import com.example.oya.newsreader.ui.newslist.ArticleAdapter
import com.example.oya.newsreader.ui.newslist.CHOSEN_ARTICLE
import com.example.oya.newsreader.ui.newslist.ListItemClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class BookmarkActivity : AppCompatActivity(), ListItemClickListener {

    lateinit var binding : ActivityListBinding

    private val viewModel : BookmarkViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Set the toolbar and enable up button
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.item_divider
            )!!)

        val articleAdapter = ArticleAdapter(this)
        binding.recycler.apply {
            adapter = articleAdapter
            addItemDecoration(itemDecoration)
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bookmarks.collectLatest {
                    if (it.isNotEmpty()) {
                        Timber.d("Bookmarks received. List size : ${it.size}")
                        articleAdapter.submitList(it)
                    } else {
                        Timber.d("Empty list received from database for bookmarks")
                    }
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    Timber.d("UIState is $state")
                    when (state) {
                        UIState.LOADING -> binding.showLoading()
                        UIState.SUCCESS -> binding.showList()
                        UIState.ERROR -> {//todo}
                        }
                    }
                }
            }
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

    override fun onBookmarkClick(article: NewsArticle) {
        toggleBookmarkState(article)
    }

    override fun onShareClick(url: String) {
        shareTheLink(url)
    }
}