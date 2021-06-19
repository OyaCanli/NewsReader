package com.example.oya.newsreader.ui.main

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.oya.newsreader.R
import com.canlioya.data.IUserPreferences
import com.example.oya.newsreader.databinding.ActivityMainBinding
import com.example.oya.newsreader.common.Constants
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userPreferences: IUserPreferences

    private var mSearchQuery: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        //Get the sorted list of sections
        val sectionList = userPreferences.getSectionListPreference()

        //Set ViewPager and TabLayout
        val sectionsPagerAdapter = SectionsPagerAdapter(this, sectionList)

        binding.viewpager.adapter = sectionsPagerAdapter

        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = sectionList[position]
        }.attach()

        //Retrieve the search query saved before rotation
        if (savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString("searchQuery")
        }
    }
}

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        //Get a back-up of search query in case user rotates the phone before submitting
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                mSearchQuery = newText
                return false
            }
        })

        *//*If there is search query saved during rotation,
        set the query again and expand the view*//*if (!TextUtils.isEmpty(mSearchQuery)) {
            *//*Back up saved query before expanding the view,
            because as soon as view is expanded search query is set to ""*//*
            val backupQuery = mSearchQuery
            searchItem.expandActionView()
            searchView.setQuery(backupQuery, false)
            searchView.isFocusable = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        *//*when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                intent.putExtra(
                    Constants.USER_CLICKED_SETTINGS_FROM,
                    MainActivity::class.java.simpleName
                )
                startActivity(intent)
            }
            R.id.action_bookmarks -> {
                val intent = Intent(this@MainActivity, BookmarksActivity::class.java)
                startActivity(intent)
            }
        }*//*
        return super.onOptionsItemSelected(item)
        }

        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            outState.putString(Constants.SEARCH_QUERY, mSearchQuery)
        }
    }*/