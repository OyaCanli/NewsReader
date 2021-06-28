package com.canlioya.data

class FakeUserPreferences : IUserPreferences {

    var _sectionListPreference = listOf("politics", "world", "technology")

    override fun getArticlePerPagePreference(): String = "5"

    override fun getSectionListPreference(): List<String> = _sectionListPreference

    override fun setSectionListPreference(set: Set<String>) {
        _sectionListPreference = set.toList()
    }

    override fun setSectionListPreference(list: List<String>) {
        _sectionListPreference = list
    }

    override fun isSyncEnabled(): Boolean {
        return true
    }

    override fun getBackUpFrequency(): Long {
        return 8
    }

    override fun shouldSyncOnlyOnWifi(): Boolean {
        return true
    }

    override fun shouldSyncOnlyWhenIdle(): Boolean {
        return true
    }

    override fun shouldSyncOnlyWhenCharging(): Boolean {
        return false
    }

    override fun isNotificationEnabled(): Boolean {
        return true
    }
}