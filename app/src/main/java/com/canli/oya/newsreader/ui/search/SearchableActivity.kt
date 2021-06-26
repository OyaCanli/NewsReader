package com.canli.oya.newsreader.ui.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import com.canli.oya.newsreader.R
import com.canli.oya.newsreader.databinding.ActivityListBinding
import com.canli.oya.newsreader.ui.details.DetailsActivity
import com.canli.oya.newsreader.ui.newslist.ArticleAdapter
import com.canli.oya.newsreader.ui.newslist.ListItemClickListener
import com.canlioya.core.model.NewsArticle
import android.app.SearchManager
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.canli.oya.newsreader.common.*
import com.canli.oya.newsreader.ui.newslist.BOOKMARK_ITEM
import com.canli.oya.newsreader.ui.newslist.REMOVE_BOOKMARK
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SearchableActivity : AppCompatActivity(), ListItemClickListener {

    private lateinit var articleAdapter: ArticleAdapter

    lateinit var binding: ActivityListBinding

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Set the toolbar and enable up button
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        var query: String? = null
        if (Intent.ACTION_SEARCH == intent.action) {
            query = intent.getStringExtra(SearchManager.QUERY)
        }

        query?.let {
            viewModel.searchInNews(it)
        }

        binding.toolbar.title = getString(R.string.search_results_for, query)

        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.item_divider
            )!!
        )

        articleAdapter = ArticleAdapter(this)
        binding.recycler.apply {
            adapter = articleAdapter
            addItemDecoration(itemDecoration)
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.results.collectLatest {
                    if (it.isNotEmpty()) {
                        Timber.d("Results received. List size : ${it.size}")
                        articleAdapter.submitList(it)
                    } else {
                        Timber.d("No results")
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
                        UIState.EMPTY -> binding.showEmpty(R.string.no_results)
                        UIState.ERROR -> {} //todo: check internet
                    }
                }
            }
        }
    }

    override fun onListItemClick(article: NewsArticle) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra(CHOSEN_ARTICLE, article)
        startActivity(intent)
    }

    override fun onBookmarkClick(position : Int, article: NewsArticle) {
        viewModel.toogleBookmarkState(position, article)
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