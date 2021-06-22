package com.example.oya.newsreader.ui.newslist


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canlioya.core.model.NewsArticle
import com.canlioya.core.model.Result
import com.example.oya.newsreader.common.UIState
import com.example.oya.newsreader.data.Interactors
import com.example.oya.newsreader.di.IODispatcher
import com.example.oya.newsreader.ui.main.SECTION_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class ArticleListViewModel @Inject constructor(private val interactors: Interactors,
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
            interactors.getNewsForSection(section!!).collectLatest { result ->
                when(result) {
                    is Result.Loading -> _uiState.value = UIState.LOADING
                    is Result.Error -> _uiState.value = UIState.ERROR
                    is Result.Success -> {
                        if(result.data.isNotEmpty()){
                            _articles.value = result.data
                            _uiState.value = UIState.SUCCESS
                            Timber.d("news for section received. list size : ${result.data.size}")
                        }
                    }
                }
            }
        }
    }

    fun saveToBookmarks(article: NewsArticle) {
        viewModelScope.launch {
            interactors.bookmarkArticle(article)
        }
    }

    fun refreshDataForSection(){
        viewModelScope.launch {
            interactors.refreshDataForSection(section!!)
        }
    }


}