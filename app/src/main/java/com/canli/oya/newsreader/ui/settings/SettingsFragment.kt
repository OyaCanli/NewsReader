package com.canli.oya.newsreader.ui.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import androidx.preference.*
import android.widget.Toast
import com.canli.oya.newsreader.data.Interactors
import com.canli.oya.newsreader.synch.SyncUtils
import com.canlioya.data.IUserPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import com.canli.oya.newsreader.R
import com.canli.oya.newsreader.notification.NotificationUtils
import java.lang.NumberFormatException
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var userPreferences: IUserPreferences

    @Inject
    lateinit var syncUtils: SyncUtils

    @Inject
    lateinit var notificationUtils : NotificationUtils

    @Inject
    lateinit var interactors: Interactors


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_main, rootKey)

        //Register a onSharedPreferenceChangeListener so that changes in the settings are applied right away
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        /*Set an OnPreferenceChangeListener to EditPreference. OnPreferenceChangeListener is called BEFORE the
        change is saved to preferences and we want to verify whether data is valid before saving it.
        That's why OnPreferenceChangeListener is better for this case.*/
        val itemPerPagePref: EditTextPreference? = findPreference(getString(R.string.pref_key_itemsPerPage))
        itemPerPagePref?.apply {
            onPreferenceChangeListener = this@SettingsFragment
            setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }

        //Set an OnPreferenceClickListener to "sort sections" item so that it opens another activity when clicked
        val sortSectionsPref: Preference? = findPreference(getString(R.string.pref_key_sort_sections))
        sortSectionsPref?.onPreferenceClickListener = this
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        /*This method is called each time there is a change in the SharedPreferences file, no matter which
        onSharedPreferenceChanged is called AFTER the changes are made to preferences. So when you get a
        preference within this method, you'll receive the freshly changed new preference value.*/

        val enableNotificationsKey = getString(R.string.pref_key_enableNotifications)
        val sectionChoicesKey = getString(R.string.pref_key_sections)
        val itemPerPageKey = getString(R.string.pref_key_itemsPerPage)

        when (key) {
            enableNotificationsKey -> {
                if(userPreferences.isNotificationEnabled()){
                    notificationUtils.scheduleNotificationJob()
                } else {
                    notificationUtils.cancelNotifications()
                }
            }
            sectionChoicesKey -> {
                Timber.d("section preferences changed")
                //Sort and save new section preferences
                val defaultSections: Set<String> =
                    resources.getStringArray(R.array.pref_section_default_values).toSet()
                val sections = sharedPreferences.getStringSet(key, defaultSections)
                userPreferences.setSectionListPreference(sections?.toSet()!!)

                refreshAllData()
            }
            itemPerPageKey -> refreshAllData()
        }
    }

    private fun refreshAllData(){
        GlobalScope.launch {
            interactors.refreshAllData()
            //Clear cached articles of the sections user doesn't want anymore
            interactors.cleanUnusedData()
        }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        /*If you have set OnPreferenceChangeListener to a preference, then whenever that preference
        is changed this method will be called. (Remember that OnSharedPreferenceChangeListener
        is called for any changes in any of preferences) Another major difference is that OnPreferenceChange
        is called BEFORE the change is saved to preferences, whereas ONSharedPreferenceChange is called AFTER
        the change is saved. For my EditTextPreference I want to verify the data BEFORE saving, in order to
        avoid a crash. That's why I preferred a OnPReferenceChangeListener for this one.*/

        val itemsPerPageKey = getString(R.string.pref_key_itemsPerPage)
        if (preference!!.key.equals(itemsPerPageKey)) {
            val stringSize = newValue as String
            val error: Toast =
                Toast.makeText(activity, R.string.warning_edittext_preference, Toast.LENGTH_SHORT)
            try {
                //Try to parse user's entry to integer
                val size = stringSize.toInt()
                //If there is an error during parsing(if user entered a text for instance, it will jump to catch part)
                if (size > 50 || size < 1) {
                    /*If user entered a number more than 50 or less than 1, show warning message
                    and RETURN, so user's entry will not be saved in this case*/
                    error.show()
                    return false
                }
            } catch (nfe: NumberFormatException) {
                //If user entered a text, show a warning message and RETURN, so user's entry will not be saved in this case
                error.show()
                return false
            }
        }
        return true
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        //This is like a clicklistener for preference items. I set it here to open a new activity
        if (preference?.key.equals(getString(R.string.pref_key_sort_sections))) {
            val intent = Intent(activity, SortSectionsActivity::class.java)
            startActivity(intent)
        }
        return false
    }

    override fun onDestroy() {
        //We need to unregister OnSharedPreferenceChangedListener in order to avoid memory leaks
        super.onDestroy()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}