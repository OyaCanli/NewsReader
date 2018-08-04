package com.example.oya.newsreader.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.adapters.SectionsPagerAdapter;
import com.example.oya.newsreader.synch.SyncTask;
import com.example.oya.newsreader.utils.Constants;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    private ArrayList<String> sectionList;
    private String mSearchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Get the preferred sections or default ones from shared preferences

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(Constants.IS_PREFERENCES_CHANGED)) {
            SyncTask.startImmediateSync(this);
            Log.d("MainActivity", "received an intent extra which says preferences are changed");
        }

        //Get the sorted list of sections
        sectionList = SortSectionsActivity.getSections(this);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), sectionList);
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        //Add tabs dynamically according to user preferences
        tabLayout.removeAllTabs();
        for(String section : sectionList){
            tabLayout.addTab(
                    tabLayout.newTab().setText(section));
        }

        //Retrieve the search query saved before rotation
        if(savedInstanceState != null){
            mSearchQuery = savedInstanceState.getString("searchQuery");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        //Get a back-up of search query in case user rotates the phone before submitting
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSearchQuery = newText;
                return false;
            }
        });

        /*If there is search query saved during rotation,
        set the query again and expand the view*/
        if(!TextUtils.isEmpty(mSearchQuery)){
            /*Back up saved query before expanding the view,
            because as soon as view is expanded search query is set to ""*/
            String backupQuery = mSearchQuery;
            searchItem.expandActionView();
            searchView.setQuery(backupQuery, false);
            searchView.setFocusable(true);
        }

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.SEARCH_QUERY, mSearchQuery);
    }
}

