package com.canlioya.data

import com.canlioya.core.model.NewsArticle

class FakeRemoteDataSource(val articles : MutableList<NewsArticle> = sampleArticles) : INetworkDataSource {

    override suspend fun getArticlesForSection(section: String): List<NewsArticle> {
        return articles.filter {
            it.section == section
        }
    }

    override suspend fun searchInNews(keywords: String): List<NewsArticle> {
        return when(keywords) {
            "Coranavirus" -> listOf(sampleWorldArticle)
            "industry" -> listOf(samplePoliticsArticle)
            "Google" -> listOf(sampleTechnologyArticle)
            else -> emptyList()
        }
    }

    override suspend fun checkHotNews(): NewsArticle? {
        return NewsArticle("hotNews", "hotNews", "hotNews", "hotNews", "hotNews", "hotNews", "hotNews", "hotNews", "hotNews", false)
    }
}