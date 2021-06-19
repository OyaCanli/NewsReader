package com.canlioya.core.usecases

import com.canlioya.core.repository.INewsRepository

class GetBookmarks(private val repository: INewsRepository) {
    operator fun invoke() = repository.getBookmarks()
}