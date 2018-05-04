package com.example.oya.newsreader.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.example.oya.newsreader.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setHasOptionsMenu(true);
        Preference pref_itemPerPage = findPreference(getString(R.string.pref_key_itemsPerPage));
        pref_itemPerPage.setOnPreferenceChangeListener(this);
        Preference pref_sortSections = findPreference(getString(R.string.pref_key_sort_sections));
        pref_sortSections.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), SortSectionsActivity.class);
                startActivity(intent);
                return false;
            }
        });
        sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);
            setPreferenceSummary(p);
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

}