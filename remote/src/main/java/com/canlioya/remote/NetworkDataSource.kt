package com.canlioya.remote


import com.canlioya.core.model.NewsArticle
import com.canlioya.core.model.Result
import com.canlioya.data.INetworkDataSource
import com.canlioya.data.IUserPreferences
import org.apache.http.client.utils.URIBuilder
import retrofit2.HttpException
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import javax.inject.Inject


class NetworkDataSource @Inject constructor(
    private val userPreferences: IUserPreferences,
    private val apiService: NewsApiService
) : INetworkDataSource {

    override suspend fun getArticlesForSection(section: String) : List<NewsArticle> {
        try {
            val response = apiService.getArticles(section, userPreferences.getArticlePerPagePreference(), userPreferences.getOrderByPreference()).response
            return response.results?.toDomainNews() ?: emptyList()
        } catch (e: HttpException) {
            println(e)
            return emptyList()
        } catch (e: IOException) {
            println(e)
            return emptyList()
        }
    }

    override suspend fun searchInNews(keywords: String): List<NewsArticle> {
        TODO("Not yet implemented")
    }
}