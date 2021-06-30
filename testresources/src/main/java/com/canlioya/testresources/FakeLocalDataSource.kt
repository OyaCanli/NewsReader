package com.canlioya.testresources

import com.canlioya.core.model.NewsArticle
import com.canlioya.data.ILocalDataSource
import com.canlioya.data.IUserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLocalDataSource(
    private val articles: MutableList<NewsArticle> = getSampleArticles(),
    private val userPreferences: IUserPreferences = FakeUserPreferences()
) : ILocalDataSource {

    override suspend fun refreshDataForSection(section: String, list: List<NewsArticle>) {
        //Clear articles from given section
        articles.removeAll {
            it.section == section
        }
        //Add new articles given for this section
        articles.addAll(list)
    }

    override suspend fun saveAsBookmark(article: NewsArticle) {
        val articleToBookmark = articles.find {
            it.articleId == article.articleId
        }
        if (articleToBookmark != null) {
            articleToBookmark.isBookmarked = true
        } else {
            articles.add(article.copy(isBookmarked = true))
        }
    }

    override suspend fun removeFromBookmarks(articleId: String) {
        val articleToBookmark = articles.find {
            it.articleId == articleId
        }
        articleToBookmark?.isBookmarked = false
    }

    override fun getArticlesForSection(section: String): Flow<List<NewsArticle>> {
        return flow {
            emit(articles.filter {
                it.section == section
            })
        }
    }

    override fun getBookmarks(): Flow<List<NewsArticle>> {
        return flow {
            emit(articles.filter {
                it.isBookmarked
            })
        }
    }

    override suspend fun clearUnusedData() {
        val section = userPreferences.getSectionListPreference()
        articles.retainAll {
            section.contains(it.section)
        }
    }
}