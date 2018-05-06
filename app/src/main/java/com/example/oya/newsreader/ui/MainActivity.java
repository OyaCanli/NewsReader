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
import com.example.oya.newsreader.utils.DatabaseUtils;
import com.example.oya.newsreader.utils.Constants;
import com.example.oya.newsreader.utils.NotificationUtils;

import java.util.ArrayList;
import java.util.Arrays;
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
        //Get the preferred sections or default ones from shared preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        ArrayList<String> sectionList = getSections();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), sectionList);
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        //Get the sorted list of sections from the adapter
        //Add tabs dynamically according to user preferences
        tabLayout.removeAllTabs();
        for (int i = 0; i < sectionList.size(); i++) {
            tabLayout.addTab(
                    tabLayout.newTab().setText(sectionList.get(i)));
        }
        if(preferences.getBoolean(getString(R.string.pref_key_enableNotifications), getResources().getBoolean(R.bool.pref_notifications_default))){
            //Schedule a background service for checking for recent news
            NotificationUtils.scheduleNewsChecker(this);
        }
        if(preferences.getBoolean(getString(R.string.pref_key_offline_reading), getResources().getBoolean(R.bool.pref_offline_reading_default))){
            //Schedule a background service for bakcing up new articles to database
            DatabaseUtils.scheduleNewsBackUp(this);
        }
        //TODO : to ask : If user disables it previously scheduled jobs are automatically cancelled or do I need to do something extra here?
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
        } else if (id == R.id.action_bookmarks){
            Intent intent = new Intent(MainActivity.this, BookmarksActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<String> getSections(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ArrayList<String> preferredSections = new ArrayList<>();
        int size = sharedPreferences.getInt("sections_size", 0);

        for(int i=0;i<size;i++) {
            preferredSections.add(sharedPreferences.getString("section_" + i, null));
        }

        for(int i = 0; i <size ; ++i){
            Log.d("SortSections", "'after retrieving arraylist" + preferredSections.get(i));
        }
        if(preferredSections.isEmpty()){
            ArrayList<String> default_sections = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.pref_section_default_values)));
            preferredSections.addAll(default_sections);
        }
        return preferredSections;
    }
}

