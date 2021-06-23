package com.example.oya.newsreader.data

import com.canlioya.core.usecases.*

data class Interactors(
    val getNewsForSection: GetNewsForSection,
    val getBookmarks: GetBookmarks,
    val toggleBookmarkState: ToggleBookmarkState,
    val refreshAllData: RefreshAllData,
    val refreshDataForSection: RefreshDataForSection,
    val searchInNews: SearchInNews,
    val cleanUnusedData: CleanUnusedData
)