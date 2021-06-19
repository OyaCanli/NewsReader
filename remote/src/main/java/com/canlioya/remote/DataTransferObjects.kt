package com.canlioya.remote

data class Root(val response : Response)

data class Response (
    val status: String?,
    val userTier: String?,
    val total: Int?,
    val startIndex: Int?,
    val pageSize: Int?,
    val currentPage: Int?,
    val pages: Int?,
    val orderBy: String?,
    val results: List<ArticleDTO>?
)

data class ArticleDTO (
    val id: String,
    val type: String?,
    val sectionId: String,
    val sectionName: String,
    val webPublicationDate: String,
    val webTitle: String,
    val webUrl: String,
    val apiUrl: String?,
    val fields: Fields?,
    val isHosted: Boolean?,
    val pillarId: String?,
    val pillarName: String?
)

data class Fields (
    val trailText: String?,
    val byline: String?,
    val body: String?,
    val thumbnail: String?
)