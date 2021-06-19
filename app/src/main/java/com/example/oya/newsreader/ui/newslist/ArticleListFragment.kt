package com.example.oya.newsreader.ui.newslist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.canlioya.core.model.NewsArticle
import com.example.oya.newsreader.R
import com.example.oya.newsreader.common.UIState
import com.example.oya.newsreader.common.showList
import com.example.oya.newsreader.common.showLoading
import com.example.oya.newsreader.databinding.FragmentListBinding
import com.example.oya.newsreader.ui.main.ArticleAdapter
import com.example.oya.newsreader.ui.main.ListItemClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber


const val CHOSEN_ARTICLE = "chosenArticle"

@AndroidEntryPoint
class ArticleListFragment : Fragment(R.layout.fragment_list), ListItemClickListener {

    private val viewModel: ArticleListViewModel by viewModels()

    private val binding by viewBinding(FragmentListBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ArticleAdapter(this)
        binding.recycler.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.articles.collectLatest {
                    if (it.isNotEmpty()) {
                        Timber.d("ARticle list received. List size : ${it.size}")
                        adapter.submitList(it)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
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

    override fun onListItemClick(view: View?, article : NewsArticle) {
        when(view?.id) {
            R.id.article_item_root -> openDetails(article)
            R.id.share -> shareTheLink(article.webUrl)
            R.id.bookmark -> saveToBookmarks(article)
        }
    }

    private fun saveToBookmarks(article: NewsArticle) {
        viewModel.saveToBookmarks(article)
    }

    private fun shareTheLink(webUrl: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, webUrl)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun openDetails(article: NewsArticle) {
        /*val intent = Intent(context, DetailsActivity::class.java)
        intent.putExtra(CHOSEN_ARTICLE, article)
        startActivity(intent)*/
    }
}