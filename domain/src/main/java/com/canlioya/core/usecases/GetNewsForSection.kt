package com.canlioya.core.usecases

import com.canlioya.core.repository.INewsRepository

class GetNewsForSection(private val repository: INewsRepository) {
    operator fun invoke(section : String) = repository.getArticlesForSection(section)
}