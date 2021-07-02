package com.canli.oya.newsreader.ui.details


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canli.oya.newsreader.data.Interactors
import com.canli.oya.newsreader.di.IODispatcher
import com.canlioya.core.model.NewsArticle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val interactors: Interactors,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    fun toggleBookmarkState(article: NewsArticle) {
        GlobalScope.launch(ioDispatcher) {
            interactors.toggleBookmarkState(article)
        }
    }
}