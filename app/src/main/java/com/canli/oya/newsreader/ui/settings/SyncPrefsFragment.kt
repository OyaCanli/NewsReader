package com.canli.oya.newsreader.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.canli.oya.newsreader.R
import com.canli.oya.newsreader.synch.SyncUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SyncPrefsFragment :
    PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var syncUtils: SyncUtils

    private var onlyOnWifiPref: Preference? = null
    private var onlyWhenIdlePref: Preference? = null
    private var onlyOnChargePref: Preference? = null
    private var backUpFrequencyPref: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_sync, rootKey)

        // Register a onSharedPreferenceChangeListener so that changes in the settings are applied right away
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        backUpFrequencyPref = findPreference(getString(R.string.pref_key_backUpFrequency))

        // Options related to back up will be disabled if user cancel back up.
        onlyOnWifiPref = findPreference(getString(R.string.only_on_wifi_key))
        onlyWhenIdlePref = findPreference(getString(R.string.pref_key_only_when_device_idle))
        onlyOnChargePref = findPreference(getString(R.string.pref_key_only_on_charge))
        val offlineEnabled = sharedPreferences.getBoolean(
            requireContext().getString(R.string.pref_key_offline_reading),
            requireContext().resources.getBoolean(R.bool.pref_offline_reading_default)
        )
        enableOrDisableBackUpPreferences(offlineEnabled)
    }

    private fun enableOrDisableBackUpPreferences(isEnabled: Boolean) {
        onlyOnWifiPref?.isEnabled = isEnabled
        onlyWhenIdlePref?.isEnabled = isEnabled
        onlyOnChargePref?.isEnabled = isEnabled
        backUpFrequencyPref?.isEnabled = isEnabled
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val enableBackUpKey = getString(R.string.pref_key_offline_reading)

        when (key) {
            enableBackUpKey -> {
                /*If user enables back-ups, enable the options related to backup and schedule backup
                If user disables backups, disable the options related to backup and cancel backup services*/
                val shouldEnable = sharedPreferences?.getBoolean(key, true) ?: true
                enableOrDisableBackUpPreferences(shouldEnable)
                if (shouldEnable) {
                    syncUtils.scheduleSyncNewsJob()
                } else {
                    syncUtils.cancelBackUps()
                }
            }
        }
    }
}
