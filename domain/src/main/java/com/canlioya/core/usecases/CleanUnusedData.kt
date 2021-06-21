package com.canlioya.core.usecases

import com.canlioya.core.repository.INewsRepository

class CleanUnusedData(private val repository: INewsRepository) {

    suspend operator fun invoke(){
        repository.clearUnusedData()
    }
}