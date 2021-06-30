package com.canli.oya.newsreader.ui.newslist


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ListenableWorker
import com.canli.oya.newsreader.common.UIState
import com.canli.oya.newsreader.data.Interactors
import com.canli.oya.newsreader.di.IODispatcher
import com.canli.oya.newsreader.ui.main.SECTION_KEY
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
class ArticleListViewModel @Inject constructor(
    private val interactors: Interactors,
    savedStateHandle: SavedStateHandle,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val section = savedStateHandle.get<String>(SECTION_KEY)

    private var _articles = MutableStateFlow<List<NewsArticle>>(emptyList())
    val articles: StateFlow<List<NewsArticle>>
        get() = _articles

    private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.LOADING)
    val uiState: StateFlow<UIState>
        get() = _uiState

    init {
        viewModelScope.launch(ioDispatcher) {
            _uiState.value = UIState.LOADING
            interactors.getNewsForSection(section!!).collectLatest {
                if (it.isNotEmpty()) {
                    _articles.value = it
                    _uiState.value = UIState.SUCCESS
                    Timber.d("news for section received. list size : ${it.size}")
                }
            }
        }
    }

    fun toggleBookmarkState(article: NewsArticle) {
        viewModelScope.launch(ioDispatcher) {
            interactors.toggleBookmarkState(article)
        }
    }

    fun refreshDataForSection() {
        viewModelScope.launch(ioDispatcher) {
            interactors.refreshDataForSection(section!!)
        }
    }


}