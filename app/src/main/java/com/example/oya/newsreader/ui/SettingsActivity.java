package com.example.oya.newsreader.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.utils.Constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class SettingsActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    String intentComingFrom = MainActivity.class.getSimpleName(); //default case
    boolean sectionsChanged = false;

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
            LinkedHashSet<String> sortedSections = sortInDefaultOrder(sections);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(Constants.PREF_SORTED_SECTIONS, sortedSections);
            editor.apply();
        }
    }

    public static LinkedHashSet<String> sortInDefaultOrder(Set<String> sections){
        LinkedHashSet<String> sortedList = new LinkedHashSet<>();
        if(sections.contains("politics"))
            sortedList.add("politics");
        if(sections.contains("world"))
            sortedList.add("world");
        if(sections.contains("business"))
            sortedList.add("business");
        if(sections.contains("technology"))
            sortedList.add("technology");
        if(sections.contains("science"))
            sortedList.add("science");
        if(sections.contains("sport"))
            sortedList.add("sport");
        if(sections.contains("football"))
            sortedList.add("football");
        if(sections.contains("music"))
            sortedList.add("music");
        if(sections.contains("culture"))
            sortedList.add("culture");
        if(sections.contains("travel"))
            sortedList.add("travel");
        if(sections.contains("fashion"))
            sortedList.add("fashion");
        return sortedList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
}
