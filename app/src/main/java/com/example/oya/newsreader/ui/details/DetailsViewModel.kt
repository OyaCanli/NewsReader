package com.example.oya.newsreader.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canlioya.core.model.NewsArticle
import com.example.oya.newsreader.data.Interactors
import com.example.oya.newsreader.di.IODispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val interactors: Interactors,
    savedStateHandle: SavedStateHandle,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    fun toggleBookmarkState(article: NewsArticle) {
        viewModelScope.launch {
            interactors.toggleBookmarkState(article)
        }
    }
}