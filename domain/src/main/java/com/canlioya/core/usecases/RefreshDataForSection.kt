package com.canlioya.core.usecases

import com.canlioya.core.repository.INewsRepository

class RefreshDataForSection(private val repository: INewsRepository) {
    suspend operator fun invoke(section : String){
        repository.refreshDataForSection(section)
    }
}