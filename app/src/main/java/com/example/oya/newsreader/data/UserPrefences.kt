package com.example.oya.newsreader.data

import android.content.Context
import com.canlioya.data.IUserPreferences
import com.example.oya.newsreader.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


const val PREFERENCE_FILE_NAME = "NewsReaderPrefs"
const val KEY_ORDER_BY = "order_by"
const val ORDER_BY_DEFAULT = "newest"
const val KEY_ARTICLE_PER_PAGE = "article_per_page"
const val ARTICLE_PER_PAGE_DEFAULT = 25
const val SECTION_LIST_KEY = "section_list_key"
const val DEFAULT_SYNC_FREQUENCY = 8

class UserPreferences @Inject constructor(@ApplicationContext val context: Context) :
    IUserPreferences {

    private val preferences = context.getSharedPreferences(PREFERENCE_FILE_NAME, 0)
    private val DEFAULT_SECTION_LIST : List<String> = listOf("politics", "world", "business", "technology", "science")

    override fun getOrderByPreference(): String {
        return preferences.getString(KEY_ORDER_BY, ORDER_BY_DEFAULT) ?: ORDER_BY_DEFAULT
    }

    override fun getArticlePerPagePreference(): Int {
        return preferences.getInt(KEY_ARTICLE_PER_PAGE, ARTICLE_PER_PAGE_DEFAULT)
    }

    override fun getSectionListPreference(): List<String> {
        val listPrefs = preferences.getString(SECTION_LIST_KEY, null)
        return if (listPrefs == null) DEFAULT_SECTION_LIST else convertSectionStringToList(listPrefs)
    }

    override fun setSectionListPreference(list : List<String>) {
        val stringVersion = convertListToString(list)
        val editor = preferences.edit()
        editor.putString(SECTION_LIST_KEY, stringVersion)
        editor.apply()
    }

    private fun convertListToString(list: List<String>): String {
        return list.joinToString(",")
    }

    private fun convertSectionStringToList(sections: String): List<String> {
        return sections.split(",")
    }

    override fun getBackUpFrequency() : Long {
        return preferences.getLong(context.getString(R.string.pref_key_backUpFrequency), 8) //todo : make sure to save it as long
    }

    override fun shouldSyncOnlyOnWifi(): Boolean {
        return preferences.getBoolean(context.getString(R.string.only_on_wifi_key), context.resources.getBoolean(R.bool.pref_only_on_wifi_default))
    }

    override fun shouldSyncOnlyWhenIdle(): Boolean {
        return preferences.getBoolean(context.getString(R.string.pref_key_only_when_device_idle), context.resources.getBoolean(R.bool.pref_only_when_device_idle_default))
    }

    override fun shouldSyncOnlyWhenCharging(): Boolean {
        return preferences.getBoolean(context.getString(R.string.pref_key_only_on_charge), context.resources.getBoolean(R.bool.pref_only_on_charge_default))
    }
}