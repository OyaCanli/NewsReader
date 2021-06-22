package com.example.oya.newsreader.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.oya.newsreader.data.Interactors
import com.example.oya.newsreader.di.IODispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val interactors: Interactors,
                                       savedStateHandle: SavedStateHandle,
                                       @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    var searchQuery : String? = null

    fun startRefreshingData(){
        GlobalScope.launch {
            interactors.refreshAllData
        }
    }

}