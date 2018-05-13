package com.example.oya.newsreader.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.synch.ScheduleSyncUtils;
import com.example.oya.newsreader.utils.NotificationUtils;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private SharedPreferences sharedPreferences;
    private Preference pref_onlyOnWifi, pref_onlyWhenIdle, pref_onlyOnCharge, pref_backUpFrequency;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setHasOptionsMenu(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        Preference pref_itemPerPage = findPreference(getString(R.string.pref_key_itemsPerPage));
        pref_itemPerPage.setOnPreferenceChangeListener(this);
        setPreferenceSummary(pref_itemPerPage);
        Preference pref_sortSections = findPreference(getString(R.string.pref_key_sort_sections));
        pref_sortSections.setOnPreferenceClickListener(this);
        Preference pref_orderBy = findPreference(getString(R.string.pref_key_orderBy));
        setPreferenceSummary(pref_orderBy);
        pref_backUpFrequency = findPreference(getString(R.string.pref_key_backUpFrequency));
        setPreferenceSummary(pref_backUpFrequency);
        pref_onlyOnWifi = findPreference(getString(R.string.only_on_wifi_key));
        pref_onlyWhenIdle = findPreference(getString(R.string.pref_key_only_when_device_idle));
        pref_onlyOnCharge = findPreference(getString(R.string.pref_key_only_on_charge));
        boolean offlineEnabled = sharedPreferences.getBoolean(getActivity().getString(R.string.pref_key_offline_reading), getActivity().getResources().getBoolean(R.bool.pref_offline_reading_default));
        if (!offlineEnabled) {
            pref_onlyOnWifi.setEnabled(false);
            pref_onlyWhenIdle.setEnabled(false);
            pref_onlyOnCharge.setEnabled(false);
            pref_backUpFrequency.setEnabled(false);
        }
    }

    private void setPreferenceSummary(Preference p) {
        if (p instanceof EditTextPreference) {
            p.setSummary(sharedPreferences.getString(p.getKey(), ""));
        } else if (p instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) p;
            int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(p.getKey(), ""));
            if (prefIndex >= 0) {
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setPreferenceSummary(findPreference(key));
        String enableBackUpKey = getString(R.string.pref_key_offline_reading);
        String enableNotificationsKey = getString(R.string.pref_key_enableNotifications);
        if (key.equals(enableBackUpKey)) {
            if (sharedPreferences.getBoolean(key, getActivity().getResources().getBoolean(R.bool.pref_offline_reading_default))) {
                pref_onlyOnWifi.setEnabled(true);
                pref_onlyWhenIdle.setEnabled(true);
                pref_onlyOnCharge.setEnabled(true);
                pref_backUpFrequency.setEnabled(true);
                ScheduleSyncUtils.scheduleNewsBackUp(getActivity());
            } else {
                pref_onlyOnWifi.setEnabled(false);
                pref_onlyWhenIdle.setEnabled(false);
                pref_onlyOnCharge.setEnabled(false);
                pref_backUpFrequency.setEnabled(false);
                ScheduleSyncUtils.cancelBackingUps(getActivity());
            }
        } else if (key.equals(enableNotificationsKey)) {
            if (sharedPreferences.getBoolean(key, getActivity().getResources().getBoolean(R.bool.pref_notifications_default))) {
                NotificationUtils.scheduleNotifications(getActivity());
            } else {
                NotificationUtils.cancelNotifications(getActivity());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String sizeKey = getString(R.string.pref_key_itemsPerPage);
        if (preference.getKey().equals(sizeKey)) {
            String stringSize = (String) newValue;
            Toast error = Toast.makeText(getActivity(), R.string.warning_edittext_preference, Toast.LENGTH_SHORT);
            try {
                int size = Integer.parseInt(stringSize);
                if (size > 50 || size < 1) {
                    error.show();
                    return false;
                }
            } catch (NumberFormatException nfe) {
                error.show();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.pref_key_sort_sections))) {
            Intent intent = new Intent(getActivity(), SortSectionsActivity.class);
            startActivity(intent);
        }
        return false;
    }


}