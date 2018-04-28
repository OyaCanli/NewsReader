package com.example.oya.newsreader.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.adapters.SectionsPagerAdapter;
import com.example.oya.newsreader.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity{

    SectionsPagerAdapter mSectionsPagerAdapter;
    Set<String> default_sections;
    SharedPreferences preferences;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "OnCreate is called");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        default_sections = new HashSet<>(Arrays.asList(getResources().getStringArray(R.array.pref_section_default_values)));
        Set<String> sections = preferences.getStringSet(getString(R.string.pref_key_sections), default_sections);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), sections);
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        mSectionsPagerAdapter.setPreferredSections(sections);
        ArrayList<String> sectionList = mSectionsPagerAdapter.getSections();
        tabLayout.removeAllTabs();
        for (int i = 0; i < sectionList.size(); i++) {
            tabLayout.addTab(
                    tabLayout.newTab().setText(sectionList.get(i)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra(Constants.USER_CLICKED_SETTINGS_FROM, MainActivity.class.getSimpleName());
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
