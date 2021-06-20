package com.example.oya.newsreader.data

import com.canlioya.core.usecases.*

data class Interactors(
    val getNewsForSection: GetNewsForSection,
    val getBookmarks: GetBookmarks,
    val bookmarkArticle: BookmarkArticle,
    val refreshAllData: RefreshAllData,
    val refreshDataForSection: RefreshDataForSection,
    val searchInNews: SearchInNews
)