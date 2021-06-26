package com.canli.oya.newsreader.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canli.oya.newsreader.common.UIState
import com.canli.oya.newsreader.data.Interactors
import com.canli.oya.newsreader.di.IODispatcher
import com.canlioya.core.model.NewsArticle
import com.canlioya.core.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val interactors: Interactors,
    savedStateHandle: SavedStateHandle,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.LOADING)
    val uiState: StateFlow<UIState>
        get() = _uiState

    private var _results = MutableStateFlow<List<NewsArticle>>(emptyList())
    val results: StateFlow<List<NewsArticle>>
        get() = _results

    fun searchInNews(query : String) {
        viewModelScope.launch {
            interactors.searchInNews(query).collectLatest { result ->
                when(result){
                    is Result.Loading -> _uiState.value = UIState.LOADING
                    is Result.Success -> {
                        if(result.data.isNotEmpty()){
                            _uiState.value = UIState.SUCCESS
                            _results.value = result.data
                        } else {
                            _uiState.value = UIState.EMPTY
                        }
                    }
                    is Result.Error -> _uiState.value = UIState.ERROR
                }
            }
        }
    }

    fun toogleBookmarkState(position : Int, article: NewsArticle) {
        Timber.d("Article is bookmarked: ${article.isBookmarked}")
        viewModelScope.launch {
            Timber.d("Article is bookmarked: ${article.isBookmarked}")
            interactors.toggleBookmarkState(article)
        }
        val results : ArrayList<NewsArticle> = ArrayList(_results.value)
        results[position] = article.copy(isBookmarked = !article.isBookmarked) //toggle state
        _results.value = results
    }

}