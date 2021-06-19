package com.example.oya.newsreader.di

import android.content.Context
import com.canlioya.core.repository.INewsRepository
import com.canlioya.core.usecases.*
import com.canlioya.data.ILocalDataSource
import com.canlioya.data.INetworkDataSource
import com.canlioya.data.IUserPreferences
import com.canlioya.data.NewsRepository
import com.canlioya.local.DBDataSource
import com.canlioya.local.database.NewsDatabase
import com.canlioya.remote.NetworkDataSource
import com.canlioya.remote.NewsApiService
import com.example.oya.newsreader.data.Interactors
import com.example.oya.newsreader.data.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideUserPreferences(@ApplicationContext context : Context) : IUserPreferences = UserPreferences(context)

    @Provides
    fun provideDatabase(@ApplicationContext context: Context) : NewsDatabase = NewsDatabase.getInstance(context)

    @Provides
    fun provideLocalDataSource(database : NewsDatabase) : ILocalDataSource = DBDataSource(database)

    @Provides
    fun provideNetworkDataSource(preferences: IUserPreferences, apiService: NewsApiService) : INetworkDataSource = NetworkDataSource(preferences, apiService)

    @Provides
    fun provideRepository(localDS: ILocalDataSource, remoteDS: INetworkDataSource, prefs: IUserPreferences) : INewsRepository = NewsRepository(localDS, remoteDS, prefs)

    @Provides
    fun provideInteractors(repo : INewsRepository) : Interactors = Interactors(GetNewsForSection(repo), GetBookmarks(repo), BookmarkArticle(repo), RefreshData(repo), SearchInNews(repo))
}