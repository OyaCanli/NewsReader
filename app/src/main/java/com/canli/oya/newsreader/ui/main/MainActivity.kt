package com.canli.oya.newsreader.ui.main

import android.app.SearchManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.canli.oya.newsreader.R
import com.canli.oya.newsreader.common.USER_CLICKED_SETTINGS_FROM
import com.canli.oya.newsreader.common.isOnline
import com.canli.oya.newsreader.common.showSnack
import com.canli.oya.newsreader.databinding.ActivityMainBinding
import com.canli.oya.newsreader.ui.bookmarks.BookmarkActivity
import com.canli.oya.newsreader.ui.settings.SettingsActivity
import com.canlioya.data.IUserPreferences
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var userPreferences: IUserPreferences

    private val viewModel: MainViewModel by viewModels()

    private var networkReceiver: NetworkReceiver? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
                viewModel.searchQuery = newText
                return false
            }
        })

        /*If there is search query saved during rotation,
        set the query again and expand the view*/
        if (viewModel.searchQuery?.isNotBlank() == true) {
            /* Back up saved query before expanding the view,
             because as soon as view is expanded search query is set to ""*/
            val backupQuery = viewModel.searchQuery
            searchItem.expandActionView()
            searchView.setQuery(backupQuery, false)
            searchView.isFocusable = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                intent.putExtra(
                    USER_CLICKED_SETTINGS_FROM,
                    MainActivity::class.java.simpleName
                )
                startActivity(intent)
            }
            R.id.action_bookmarks -> {
                val intent = Intent(this@MainActivity, BookmarkActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * When fetching failed with a network error, we start listening for
     * network state by registering a broadcast receiver for CONNECTIVITY_CHANGE
     */
    private fun startListeningNetworkState() {
        Timber.d("Start listening network state")
        if (networkReceiver == null) {
            networkReceiver = NetworkReceiver()
        }
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(networkReceiver, intentFilter)
    }

    /**
     * When connection is reestablished or the user quits the app
     * we unregister from the broadcast receiver
     */
    private fun stopListeningNetworkState() {
        Timber.d("Stop listening network state")
        networkReceiver?.let {
            unregisterReceiver(it)
            networkReceiver = null
        }
    }

    /**
     * Broadcast receiver for listening to network state.
     * When triggered, we check if network is available
     * and if available we start fetching again, we show
     * a snack for informing the user and we unregister
     * from the broadcast receiver
     */
    inner class NetworkReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.d("Broadcast received for network state")
            if (isOnline(context)) {
                Timber.d("is online")
                stopListeningNetworkState()
                viewModel.startRefreshingData()
                binding.mainContent.showSnack(
                    text = R.string.internet_is_back,
                    backgroundColor = android.R.color.holo_green_light
                )
            }
        }
    }

    /**
     * If user quits the app while broadcast receiver is active,
     * we unregister not to waste system resources
     */
    override fun onStop() {
        super.onStop()
        stopListeningNetworkState()
    }

    /**
     * When user comes back to app
     * we check internet connection and it
     * there is no internet we start listening network state
     */
    override fun onStart() {
        super.onStart()
        if (!isOnline(this)) {
            binding.root.showSnack(
                text = R.string.no_internet_warning,
                length = Snackbar.LENGTH_INDEFINITE,
                backgroundColor = R.color.colorPrimaryDark
            )
            startListeningNetworkState()
        }
    }

}