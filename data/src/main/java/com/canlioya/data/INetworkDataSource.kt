package com.canlioya.data

import com.canlioya.core.model.NewsArticle


interface INetworkDataSource {

    suspend fun getArticlesForSection(section : String) : List<NewsArticle>

    suspend fun searchInNews(keywords : String) : List<NewsArticle>

    suspend fun checkHotNews(): NewsArticle?

}