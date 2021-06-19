package com.canlioya.core.usecases

import com.canlioya.core.model.NewsArticle
import com.canlioya.core.repository.INewsRepository

class BookmarkArticle(private val repository: INewsRepository) {
    suspend operator fun invoke(article : NewsArticle) {
        repository.saveToBookmarks(article)
    }
}