package com.canlioya.remote


import com.canlioya.core.model.NewsArticle
import com.canlioya.data.INetworkDataSource
import com.canlioya.data.IUserPreferences
import org.apache.http.client.utils.URIBuilder
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import javax.inject.Inject


class NetworkDataSource @Inject constructor(
    private val userPreferences: IUserPreferences,
    private val apiService: NewsApiService
) : INetworkDataSource {

    override suspend fun getArticlesForSection(section: String) : List<NewsArticle> {
        var results : List<NewsArticle> = emptyList()
        try {
            val response = apiService.getArticles(section, userPreferences.getArticlePerPagePreference(), userPreferences.getOrderByPreference()).response
            results = response.results?.toDomainNews() ?: emptyList()
        } catch(e : IOException){
            println("exception catched")
            println(e)
        }

        return results
    }

    override suspend fun searchInNews(keywords: String): List<NewsArticle> {
        TODO("Not yet implemented")
    }
}