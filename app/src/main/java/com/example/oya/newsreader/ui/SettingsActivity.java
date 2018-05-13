package com.example.oya.newsreader.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ViewFlipper;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.data.NewsDbHelper;
import com.example.oya.newsreader.synch.SyncTask;
import com.example.oya.newsreader.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private String intentComingFrom = MainActivity.class.getSimpleName(); //default case
    private boolean preferencesChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ViewFlipper vf = findViewById(R.id.main_flipper);
        vf.setDisplayedChild(2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.settings);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) intentComingFrom = bundle.getString(Constants.USER_CLICKED_SETTINGS_FROM);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //User might have come to settings from several places.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (intentComingFrom.equals(MainActivity.class.getSimpleName())
                    || intentComingFrom.equals(SortSectionsActivity.class.getSimpleName())) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(Constants.IS_PREFERENCES_CHANGED, preferencesChanged);
                startActivity(intent);
            } else {
                onBackPressed();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String sectionChoicesKey = getString(R.string.pref_key_sections);
        preferencesChanged = true;
        final List<String> sortedSections;
        if (key.equals(sectionChoicesKey)) {
            //If sections are changed, sort and save them in sharedPreferences
            Set<String> default_sections = new HashSet<>(Arrays.asList(getResources().getStringArray(R.array.pref_section_default_values)));
            Set<String> sections = sharedPreferences.getStringSet(key, default_sections);
            sortedSections = sortInDefaultOrder(sections, this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("sections_size", sortedSections.size());
            for(int i=0;i<sortedSections.size();i++) {
                editor.putString("section_" + i, sortedSections.get(i));
            }
            editor.apply();
            //Clear cached articles of the sections user doesn't want anymore
            NewsDbHelper dbHelper = new NewsDbHelper(this);
            dbHelper.clearCachedArticles(this);
        }
        //Restart loading when preferences are changed
        if (thereIsConnection()) {
            SyncTask.startImmediateSync(this);
        }
    }

    private boolean thereIsConnection() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        return networkInfo != null && networkInfo.isConnected();
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
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
}
