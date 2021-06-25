package com.canli.oya.newsreader.data

import android.content.Context
import android.content.SharedPreferences
import com.canli.oya.newsreader.R
import com.canlioya.data.IUserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.lang.NumberFormatException
import java.util.*
import javax.inject.Inject



const val ORDER_BY_DEFAULT = "newest"
const val ARTICLE_PER_PAGE_DEFAULT = "20"
const val SORTED_SECTIONS_KEY = "sorted_section_key"
const val DEFAULT_SYNC_FREQUENCY = 8L

class UserPreferences @Inject constructor(@ApplicationContext private val context: Context,
                                          private val preferences : SharedPreferences)
    : IUserPreferences {

    private val defaultSections : List<String> = context.resources.getStringArray(R.array.pref_section_default_values).toCollection(ArrayList())

    override fun getOrderByPreference(): String {
        return preferences.getString(context.getString(R.string.pref_key_orderBy), ORDER_BY_DEFAULT) ?: ORDER_BY_DEFAULT
    }

    override fun getArticlePerPagePreference(): String {
        return preferences.getString(context.getString(R.string.pref_key_itemsPerPage), ARTICLE_PER_PAGE_DEFAULT) ?: ARTICLE_PER_PAGE_DEFAULT
    }

    override fun getSectionListPreference(): List<String> {
        val listPrefs = preferences.getString(SORTED_SECTIONS_KEY, null)
        return if (listPrefs == null) defaultSections else convertSectionStringToList(listPrefs)
    }

    override fun setSectionListPreference(list : Set<String>) {
        val stringVersion = list.joinToString(",")
        saveToPrefs(stringVersion)
    }

    override fun setSectionListPreference(list : List<String>) {
        val stringVersion = list.joinToString(",")
        saveToPrefs(stringVersion)
    }

    override fun isSyncEnabled(): Boolean {
        return preferences.getBoolean(context.getString(R.string.pref_key_offline_reading), true)
    }

    private fun saveToPrefs(stringVersion: String) {
        val editor = preferences.edit()
        editor.putString(SORTED_SECTIONS_KEY, stringVersion)
        editor.apply()
    }

    private fun convertSectionStringToList(sections: String): List<String> {
        return sections.split(",")
    }

    override fun getBackUpFrequency() : Long {
        val backUpPref = preferences.getString(context.getString(R.string.pref_key_backUpFrequency), "8")
        var intVersion = DEFAULT_SYNC_FREQUENCY
        try {
            //In fact this is already validated before being saved. This is a double check.
            intVersion = backUpPref?.toLong() ?: DEFAULT_SYNC_FREQUENCY
        } catch (e : NumberFormatException){
            Timber.e(e)
        }
        return intVersion
    }

    override fun shouldSyncOnlyOnWifi(): Boolean {
        return preferences.getBoolean(context.getString(R.string.only_on_wifi_key), true)
    }

    override fun shouldSyncOnlyWhenIdle(): Boolean {
        return preferences.getBoolean(context.getString(R.string.pref_key_only_when_device_idle), true)
    }

    override fun shouldSyncOnlyWhenCharging(): Boolean {
        return preferences.getBoolean(context.getString(R.string.pref_key_only_on_charge), false)
    }

    override fun isNotificationEnabled(): Boolean {
        return preferences.getBoolean(context.getString(R.string.pref_key_enableNotifications), true)
    }
}