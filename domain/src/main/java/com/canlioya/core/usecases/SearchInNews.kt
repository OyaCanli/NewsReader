package com.canlioya.core.usecases

import com.canlioya.core.repository.INewsRepository

class SearchInNews(private val repository: INewsRepository) {
    suspend operator fun invoke(keyword : String) = repository.searchInNews(keyword)
}