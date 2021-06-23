package com.example.oya.newsreader.ui.newslist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.canlioya.core.model.NewsArticle
import com.example.oya.newsreader.R
import com.example.oya.newsreader.common.UIState
import com.example.oya.newsreader.common.showList
import com.example.oya.newsreader.common.showLoading
import com.example.oya.newsreader.data.Interactors
import com.example.oya.newsreader.databinding.FragmentListBinding
import com.example.oya.newsreader.ui.details.DetailsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


const val CHOSEN_ARTICLE = "chosenArticle"

@AndroidEntryPoint
class ArticleListFragment : Fragment(R.layout.fragment_list), ListItemClickListener,
    SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: ArticleListViewModel by viewModels()

    private val binding by viewBinding(FragmentListBinding::bind)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val articleAdapter = ArticleAdapter(this)

        binding.swipeToRefresh.setOnRefreshListener(this)

        val itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(getDrawable(requireContext(), R.drawable.item_divider)!!)

        binding.recycler.apply {
            adapter = articleAdapter
            addItemDecoration(itemDecoration)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.articles.collectLatest {
                    if (it.isNotEmpty()) {
                        Timber.d("Article list received. List size : ${it.size}")
                        binding.swipeToRefresh.isRefreshing = false
                        articleAdapter.submitList(it)
                    } else {
                        Timber.d("Empty list received from database")
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
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

    override fun onListItemClick(article: NewsArticle) {
        openDetails(article)
    }

    override fun onBookmarkClick(article: NewsArticle) {
        saveToBookmarks(article)
    }

    override fun onShareClick(url: String) {
        shareTheLink(url)
    }

    private fun saveToBookmarks(article: NewsArticle) {
        Timber.d("bookmark is clicked")
        viewModel.toggleBookmarkState(article)
    }

    private fun shareTheLink(webUrl: String) {
        Timber.d("share link is clicked")
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, webUrl)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun openDetails(article: NewsArticle) {
        val intent = Intent(context, DetailsActivity::class.java)
        intent.putExtra(CHOSEN_ARTICLE, article)
        startActivity(intent)
    }

    override fun onRefresh() {
        viewModel.refreshDataForSection()
    }
}