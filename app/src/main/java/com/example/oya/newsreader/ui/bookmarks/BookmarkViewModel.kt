package com.example.oya.newsreader.ui.bookmarks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canlioya.core.model.NewsArticle
import com.example.oya.newsreader.common.UIState
import com.example.oya.newsreader.data.Interactors
import com.example.oya.newsreader.di.IODispatcher
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
    savedStateHandle: SavedStateHandle,
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
            _uiState.value = UIState.LOADING
            interactors.getBookmarks().collectLatest {
                if (it.isNotEmpty()) {
                    _bookmarks.value = it
                    _uiState.value = UIState.SUCCESS
                    Timber.d("bookmarks received. list size : ${it.size}")
                }
            }
        }
    }

    fun toggleBookmarkState(article: NewsArticle) {
        viewModelScope.launch {
            interactors.toggleBookmarkState(article)
        }
    }
}