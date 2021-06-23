package com.example.oya.newsreader.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
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
    fun provideUserPreferences(@ApplicationContext context : Context, preferences : SharedPreferences) : IUserPreferences = UserPreferences(context, preferences)

    @Provides
    fun provideDatabase(@ApplicationContext context: Context) : NewsDatabase = NewsDatabase.getInstance(context)

    @Provides
    fun provideLocalDataSource(database : NewsDatabase, prefs: IUserPreferences) : ILocalDataSource = DBDataSource(database, prefs)

    @Provides
    fun provideNetworkDataSource(preferences: IUserPreferences, apiService: NewsApiService) : INetworkDataSource = NetworkDataSource(preferences, apiService)

    @Provides
    fun provideRepository(localDS: ILocalDataSource, remoteDS: INetworkDataSource, prefs: IUserPreferences) : INewsRepository = NewsRepository(localDS, remoteDS, prefs)

    @Provides
    fun provideInteractors(repo : INewsRepository) : Interactors = Interactors(GetNewsForSection(repo), GetBookmarks(repo), ToggleBookmarkState(repo),
        RefreshAllData(repo), RefreshDataForSection(repo),SearchInNews(repo), CleanUnusedData(repo))

    @Provides
    fun getSharedPreferences(@ApplicationContext context : Context) : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
}