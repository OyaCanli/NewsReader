package com.canlioya.remote

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url


/**
 * Interface used by Retrofit
 */
interface NewsApiService {
    /**
     * Returns a Retrofit callback that delivers a response which includes the list of articles
     * @param albumId
     */
    @GET("search?api-key=test&show-fields=byline%2CtrailText%2Cthumbnail%2Cbody")
    suspend fun getArticles(@Query("section") section: String,
                            @Query("page-size") pageSize : String,
                            @Query("order-by") orderBy : String) : Root

    @GET
    suspend fun getLatestNews(@Url url : String) : Root

    @GET("search?api-key=test&show-fields=byline%2CtrailText%2Cthumbnail%2Cbody&order-by=relevance")
    suspend fun searchInNews(@Query("q") query: String,
                             @Query("page-size") pageSize : String) : Root

}