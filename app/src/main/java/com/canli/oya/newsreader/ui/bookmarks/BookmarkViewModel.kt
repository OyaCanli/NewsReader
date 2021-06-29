package com.canli.oya.newsreader.ui.bookmarks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canli.oya.newsreader.common.UIState
import com.canli.oya.newsreader.data.Interactors
import com.canli.oya.newsreader.di.IODispatcher
import com.canlioya.core.model.NewsArticle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val interactors: Interactors,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private var _bookmarks = MutableStateFlow<List<NewsArticle>>(emptyList())
    val bookmarks: StateFlow<List<NewsArticle>>
        get() = _bookmarks

    private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.LOADING)
    val uiState: StateFlow<UIState>
        get() = _uiState

    init {
        viewModelScope.launch(ioDispatcher) {
            startCollectingBookmarks()
        }
    }

    suspend fun startCollectingBookmarks() {
        _uiState.value = UIState.LOADING
        interactors.getBookmarks().collectLatest {
            if (it.isNotEmpty()) {
                _bookmarks.value = it
                _uiState.value = UIState.SUCCESS
                Timber.d("bookmarks received. list size : ${it.size}")
            } else {
                _uiState.value = UIState.EMPTY
            }
        }
    }

    fun toggleBookmarkState(article: NewsArticle) {
        viewModelScope.launch {
            interactors.toggleBookmarkState(article)
        }
    }
}