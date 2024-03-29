package com.canli.oya.newsreader.ui.main

import androidx.lifecycle.ViewModel
import com.canli.oya.newsreader.data.Interactors
import com.canli.oya.newsreader.di.IODispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val interactors: Interactors,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    var searchQuery: String? = null

    fun startRefreshingData() {
        GlobalScope.launch {
            interactors.refreshAllData
        }
    }
}
