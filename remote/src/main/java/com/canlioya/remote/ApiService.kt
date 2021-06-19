package com.canlioya.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url


/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object pointing to the desired URL
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

/**
 * Interface used by Retrofit
 */
interface NewsApiService {
    /**
     * Returns a Retrofit callback that delivers a response which includes the list of articles
     * @param albumId
     */
    @GET("search?api-key=test&show-fields=byline%2CtrailText%2Cthumbnail%2Cbody")
    suspend fun getArticles(@Query("section") section: String, @Query("page-size") pageSize : Int, @Query("order-by") orderBy : String) : Root
}

/**
 * Interface to provide an access to retrofitService instance
 * Extracted as an interface to be doubled in tests
 */
interface IApiProvider {
    val retrofitService : NewsApiService
}

/**
 * Singleton utility class to provide an access to retrofitService instance
 *
 */
object ApiProvider : IApiProvider {
    /**
     * Returns an instance of Retrofit api service, created lazily and cached
     */
    override val retrofitService : NewsApiService by lazy {
        retrofit.create(NewsApiService::class.java)
    }
}