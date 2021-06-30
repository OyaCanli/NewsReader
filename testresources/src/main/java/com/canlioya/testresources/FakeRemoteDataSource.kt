package com.canlioya.testresources

import com.canlioya.core.model.NewsArticle
import com.canlioya.data.INetworkDataSource

class FakeRemoteDataSource(val articles : MutableList<NewsArticle> = getSampleArticles()) :
    INetworkDataSource {

    override suspend fun getArticlesForSection(section: String): List<NewsArticle> {
        return articles.filter {
            it.section == section
        }
    }

    override suspend fun searchInNews(keywords: String): List<NewsArticle> {
        return when(keywords) {
            "Coranavirus" -> listOf(getSampleWorldArticle())
            "industry" -> listOf(getSamplePoliticsArticle())
            "Google" -> listOf(getSampleTechnologyArticle())
            else -> emptyList()
        }
    }

    override suspend fun checkHotNews(): NewsArticle? {
        return NewsArticle("hotNews", "hotNews", "hotNews", "hotNews", "hotNews", "hotNews", "hotNews", "hotNews", "hotNews", false)
    }
}