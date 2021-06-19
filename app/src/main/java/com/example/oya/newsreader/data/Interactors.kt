package com.example.oya.newsreader.data

import com.canlioya.core.usecases.*

data class Interactors(
    val getNewsForSection: GetNewsForSection,
    val getBookmarks: GetBookmarks,
    val bookmarkArticle: BookmarkArticle,
    val refreshData: RefreshData,
    val searchInNews: SearchInNews
)