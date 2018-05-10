package com.example.oya.newsreader.ui;

import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.data.NewsDbHelper;
import com.example.oya.newsreader.utils.Constants;
import com.example.oya.newsreader.utils.AllSectionsLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private String intentComingFrom = MainActivity.class.getSimpleName(); //default case
    private boolean sectionsChanged = false;

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
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if ((intentComingFrom.equals(MainActivity.class.getSimpleName()) && sectionsChanged)
                    || intentComingFrom.equals(SortSectionsActivity.class.getSimpleName())) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(this, R.string.sections_changed_info, Toast.LENGTH_SHORT).show();
            } else {
                onBackPressed();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String sectionChoicesKey = getString(R.string.pref_key_sections);
        if (key.equals(sectionChoicesKey)) {
            sectionsChanged = true;
            Set<String> default_sections = new HashSet<>(Arrays.asList(getResources().getStringArray(R.array.pref_section_default_values)));
            Set<String> sections = sharedPreferences.getStringSet(key, default_sections);
            final List<String> sortedSections = sortInDefaultOrder(sections, this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("sections_size", sortedSections.size());
            for(int i=0;i<sortedSections.size();i++) {
                editor.putString("section_" + i, sortedSections.get(i));
            }
            editor.apply();
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(Constants.SYNCH_CHANGED_LOADER_ID, null, new LoaderManager.LoaderCallbacks<Object>() {
                @Override
                public Loader<Object> onCreateLoader(int id, Bundle args) {
                    return new AllSectionsLoader(SettingsActivity.this, sortedSections);
                }

                @Override
                public void onLoadFinished(Loader<Object> loader, Object data) {
                }

                @Override
                public void onLoaderReset(Loader<Object> loader) {
                }
            });
            //Clear cached articles of the sections user doesn't want anymore
            NewsDbHelper dbHelper = new NewsDbHelper(this, null);
            dbHelper.clearCachedArticles(this);
        }
    }

    private static ArrayList<String> sortInDefaultOrder(Set<String> sections, Context context){
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
