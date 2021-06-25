package com.canlioya.data

interface IUserPreferences {

    fun getOrderByPreference(): String

    fun getArticlePerPagePreference(): Int

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