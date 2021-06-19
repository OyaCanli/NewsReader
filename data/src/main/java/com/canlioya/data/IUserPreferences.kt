package com.canlioya.data

interface IUserPreferences {

    fun getOrderByPreference(): String

    fun getArticlePerPagePreference(): Int

    fun getSectionListPreference() : List<String>

    fun setSectionListPreference(list : List<String>)

    fun getBackUpFrequency(): Long

    fun shouldSyncOnlyOnWifi() : Boolean

    fun shouldSyncOnlyWhenIdle() : Boolean

    fun shouldSyncOnlyWhenCharging() : Boolean
}