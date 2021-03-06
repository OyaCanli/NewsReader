package com.canlioya.data

interface IUserPreferences {

    fun getArticlePerPagePreference(): String

    fun getSectionListPreference() : List<String>

    fun setSectionListPreference(list : Set<String>)

    fun setSectionListPreference(list : List<String>)

    fun isSyncEnabled() : Boolean

    fun getBackUpFrequency(): Long

    fun shouldSyncOnlyOnWifi() : Boolean

    fun shouldSyncOnlyWhenIdle() : Boolean

    fun shouldSyncOnlyWhenCharging() : Boolean

    fun isNotificationEnabled() : Boolean
}