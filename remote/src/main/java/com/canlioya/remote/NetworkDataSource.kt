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
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class NetworkDataSource @Inject constructor(
    private val userPreferences: IUserPreferences,
    private val apiService: NewsApiService
) : INetworkDataSource {

    override suspend fun getArticlesForSection(section: String): List<NewsArticle> {
        try {
            val response = apiService.getArticles(
                section,
                userPreferences.getArticlePerPagePreference(),
                userPreferences.getOrderByPreference()
            ).response
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
        val response = apiService.searchInNews(keywords, "25").response
        return response.results?.toDomainNews() ?: emptyList()
    }

    override suspend fun checkHotNews(): NewsArticle? {
        return try {
            val results = apiService.getLatestNews(buildUrlForNotification()).response.results
            if (results?.isNotEmpty() == true) {
                results[0].toNewsArticle()
            } else {
                null
            }
        } catch (e: HttpException) {
            println(e)
            null
        } catch (e: IOException) {
            println(e)
            null
        }
    }

    private fun buildUrlForNotification(): String {
        val timeString = getFormattedTime()
        println("formatted time :$timeString")

        val builtUri = URIBuilder(BASE_URL)
            .setCustomQuery("from-date=$timeString")
            .addParameter(GUARDIAN_API_KEY, GUARDIAN_API_VALUE)
            .addParameter(SHOW_FIELDS_KEY, SHOW_FIELDS_VALUE)
            .addParameter(SECTION_PARAM, getSectionsString())
            .addParameter(ORDER_BY_PARAM, ORDER_BY_DEFAULT)
            .addParameter(PAGE_SIZE_PARAM, "1")
            .build()
            .toURL()

        println("URL : $builtUri")
        return builtUri.toString()
    }

    private fun getSectionsString() = userPreferences.getSectionListPreference().joinToString("|")

    private fun getFormattedTime(): String {
        val nowInMillis = Calendar.getInstance().timeInMillis
        val interval = TimeUnit.MINUTES.toMillis(30L)

        val timeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val timeZone = TimeZone.getTimeZone("GMT")
        timeFormat.timeZone = timeZone

        return timeFormat.format(nowInMillis - interval)
    }

}