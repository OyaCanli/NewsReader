package com.example.oya.newsreader.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.data.NewsDbHelper;
import com.example.oya.newsreader.synch.ScheduleSyncUtils;
import com.example.oya.newsreader.synch.SyncTask;
import com.example.oya.newsreader.utils.NotificationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private SharedPreferences sharedPreferences;
    private Preference pref_onlyOnWifi, pref_onlyWhenIdle, pref_onlyOnCharge, pref_backUpFrequency;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setHasOptionsMenu(true);
        //Register a onSharedPreferenceChangeListener so that changes in the settings are applied right away
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        /*Set an OnPreferenceChangeListener to EditPreference. OnPreferenceChangeListener is called BEFORE the
        change is saved to preferences and we want to verify whether data is valid before saving it.
        That's why OnPreferenceChangeListener is better for this case.*/
        Preference pref_itemPerPage = findPreference(getString(R.string.pref_key_itemsPerPage));
        pref_itemPerPage.setOnPreferenceChangeListener(this);
        setPreferenceSummary(pref_itemPerPage);
        //Set an OnPreferenceClickListener to "sort sections" item so that it opens another activity when clicked
        Preference pref_sortSections = findPreference(getString(R.string.pref_key_sort_sections));
        pref_sortSections.setOnPreferenceClickListener(this);
        //Set summaries on ListPreference items (no need to do that for checkbox preferences)
        Preference pref_orderBy = findPreference(getString(R.string.pref_key_orderBy));
        setPreferenceSummary(pref_orderBy);
        pref_backUpFrequency = findPreference(getString(R.string.pref_key_backUpFrequency));
        setPreferenceSummary(pref_backUpFrequency);
        //Options related to back up will be disabled if user cancel back up.
        pref_onlyOnWifi = findPreference(getString(R.string.only_on_wifi_key));
        pref_onlyWhenIdle = findPreference(getString(R.string.pref_key_only_when_device_idle));
        pref_onlyOnCharge = findPreference(getString(R.string.pref_key_only_on_charge));
        boolean offlineEnabled = sharedPreferences.getBoolean(getActivity().getString(R.string.pref_key_offline_reading), getActivity().getResources().getBoolean(R.bool.pref_offline_reading_default));
        if (!offlineEnabled) {
            enableOrDisableBackUpPreferences(false);
        }
    }

    private void setPreferenceSummary(Preference p) {
        //Preference summaries for checkboxes are automatic, but for EditText and ListPreferences we need to deal with it programmatically
        if (p instanceof EditTextPreference) {
            p.setSummary(sharedPreferences.getString(p.getKey(), ""));
        } else if (p instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) p;
            /*This confusing trick here is needed because LABELS of options are different than their values in my case.
            So I couldn't simply set the values to the summary.
            Even if it might be same in your case, in case of localization you'll still need this trick. */
            int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(p.getKey(), ""));
            if (prefIndex >= 0) {
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        /*This method is called each time there is a change in the SharedPreferences file, no matter which
        onSharedPreferenceChanged is called AFTER the changes are made to preferences. So when you get a
        preference within this method, you'll receive the freshly changed new preference value.*/

        setPreferenceSummary(findPreference(key)); //Update the preference summary of the item that changed

        final String enableBackUpKey = getString(R.string.pref_key_offline_reading);
        final String enableNotificationsKey = getString(R.string.pref_key_enableNotifications);
        final String sectionChoicesKey = getString(R.string.pref_key_sections);
        //Take different action according to the key changed
        if (key.equals(enableBackUpKey)) {
            /*If user enables back-ups, enable the options related to backup and schedule backup
            If user disables backups, disable the options related to backup and cancel backup services*/
            if (sharedPreferences.getBoolean(key, getActivity().getResources().getBoolean(R.bool.pref_offline_reading_default))) {
               enableOrDisableBackUpPreferences(true);
                ScheduleSyncUtils.scheduleNewsBackUp(getActivity());
            } else {
                enableOrDisableBackUpPreferences(false);
                ScheduleSyncUtils.cancelBackingUps(getActivity());
            }
        } else if (key.equals(enableNotificationsKey)) {
            //If user enables notifications, schedule notifications. If user disable notification, cancel notification services.
            if (sharedPreferences.getBoolean(key, getActivity().getResources().getBoolean(R.bool.pref_notifications_default))) {
                NotificationUtils.scheduleNotifications(getActivity());
            } else {
                NotificationUtils.cancelNotifications(getActivity());
            }
        } else if(key.equals(sectionChoicesKey)) {
            //Sort and save new section preferences
            Set<String> default_sections = new HashSet<>(Arrays.asList(getResources().getStringArray(R.array.pref_section_default_values)));
            Set<String> sections = sharedPreferences.getStringSet(key, default_sections);
            List<String> sortedSections = sortInDefaultOrder(sections, getActivity());
            //Since we can't store an ArrayList in SharedPreferences I had to do this trick that I found in stackoverflow.
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("sections_size", sortedSections.size());
            for(int i=0;i<sortedSections.size();i++) {
                editor.putString("section_" + i, sortedSections.get(i));
            }
            editor.apply();
            //Clear cached articles of the sections user doesn't want anymore
            NewsDbHelper dbHelper = new NewsDbHelper(getActivity());
            dbHelper.clearCachedArticles(getActivity());
        }
        /*Since preferences are changed, start synchronization again
        (i.e. refresh the news in the database according to user's new choices)*/
        if (thereIsConnection()) {
            SyncTask.startImmediateSync(getActivity());
        }
    }

    private boolean thereIsConnection() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        return networkInfo != null && networkInfo.isConnected();
    }

    private void enableOrDisableBackUpPreferences(boolean isEnabled){
        pref_onlyOnWifi.setEnabled(isEnabled);
        pref_onlyWhenIdle.setEnabled(isEnabled);
        pref_onlyOnCharge.setEnabled(isEnabled);
        pref_backUpFrequency.setEnabled(isEnabled);
    }

    private static ArrayList<String> sortInDefaultOrder(Set<String> sections, Context context){
        /*MultiSelectListPreference saves info in an unordered set and they can come up in a different
        order each time. So I'm sorting them here in a default order which I esteem to be appropriate.
        User can still sort them as they wish in the sortSections activity*/
        ArrayList<String> sortedList = new ArrayList<>();
        if(sections.contains(context.getString(R.string.politics).toLowerCase()))
            sortedList.add(context.getString(R.string.politics).toLowerCase());
        if(sections.contains(context.getString(R.string.world).toLowerCase()))
            sortedList.add(context.getString(R.string.world).toLowerCase());
        if(sections.contains(context.getString(R.string.business).toLowerCase()))
            sortedList.add(context.getString(R.string.business).toLowerCase());
        if(sections.contains(context.getString(R.string.technology).toLowerCase()))
            sortedList.add(context.getString(R.string.technology).toLowerCase());
        if(sections.contains(context.getString(R.string.science).toLowerCase()))
            sortedList.add(context.getString(R.string.science).toLowerCase());
        if(sections.contains(context.getString(R.string.sport).toLowerCase()))
            sortedList.add(context.getString(R.string.sport).toLowerCase());
        if(sections.contains(context.getString(R.string.football).toLowerCase()))
            sortedList.add(context.getString(R.string.football).toLowerCase());
        if(sections.contains(context.getString(R.string.music).toLowerCase()))
            sortedList.add(context.getString(R.string.music).toLowerCase());
        if(sections.contains(context.getString(R.string.culture).toLowerCase()))
            sortedList.add(context.getString(R.string.culture).toLowerCase());
        if(sections.contains(context.getString(R.string.travel).toLowerCase()))
            sortedList.add(context.getString(R.string.travel).toLowerCase());
        if(sections.contains(context.getString(R.string.art_and_design).toLowerCase()))
            sortedList.add(context.getString(R.string.art_and_design).toLowerCase());
        if(sections.contains(context.getString(R.string.books).toLowerCase()))
            sortedList.add(context.getString(R.string.books).toLowerCase());
        if(sections.contains(context.getString(R.string.environment).toLowerCase()))
            sortedList.add(context.getString(R.string.environment).toLowerCase());
        if(sections.contains(context.getString(R.string.education).toLowerCase()))
            sortedList.add(context.getString(R.string.education).toLowerCase());
        if(sections.contains(context.getString(R.string.film).toLowerCase()))
            sortedList.add(context.getString(R.string.film).toLowerCase());
        if(sections.contains(context.getString(R.string.fashion).toLowerCase()))
            sortedList.add(context.getString(R.string.fashion).toLowerCase());
        return sortedList;
    }

    @Override
    public void onDestroy() {
        //We need to unregister OnSharedPreferenceChangedListener in order to avoid memory leaks
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        /*If you have set OnPreferenceChangeListener to a preference, then whenever that preference
        is changed this method will be called. (Remember that OnSharedPreferenceChangeListener
        is called for any changes in any of preferences) Another major difference is that OnPreferenceChange
        is called BEFORE the change is saved to preferences, whereas ONSharedPreferenceChange is called AFTER
        the change is saved. For my EditTextPreference I want to verify the data BEFORE saving, in order to
        avoid a crash. That's why I preferred a OnPReferenceChangeListener for this one.*/

        String sizeKey = getString(R.string.pref_key_itemsPerPage);
        if (preference.getKey().equals(sizeKey)) {
            String stringSize = (String) newValue;
            Toast error = Toast.makeText(getActivity(), R.string.warning_edittext_preference, Toast.LENGTH_SHORT);
            try {
                //Try to parse user's entry to integer
                int size = Integer.parseInt(stringSize);
                //If there is an error during parsing(if user entered a text for instance, it will jump to catch part)
                //If entry is parse to integer successfully then it will go the if statement
                if (size > 50 || size < 1) {
                    /*If user entered a number more than 50 or less than 1, show warning message
                    and RETURN, so user's entry will not be saved in this case*/
                    error.show();
                    return false;
                }
            } catch (NumberFormatException nfe) {
                //If user entered a text, show a warning message and RETURN, so user's entry will not be saved in this case
                error.show();
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        //This is like a clicklistener for preference items. I set it here to open a new activity
        if (preference.getKey().equals(getString(R.string.pref_key_sort_sections))) {
            Intent intent = new Intent(getActivity(), SortSectionsActivity.class);
            startActivity(intent);
        }
        return false;
    }


}