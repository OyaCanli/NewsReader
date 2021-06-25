package com.canli.oya.newsreader.ui.settings

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import android.content.Intent
import android.view.MenuItem
import com.canli.oya.newsreader.R
import com.canli.oya.newsreader.common.USER_CLICKED_SETTINGS_FROM
import com.canli.oya.newsreader.databinding.ActivitySettingsBinding
import com.canli.oya.newsreader.ui.main.MainActivity


@AndroidEntryPoint
class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var binding : ActivitySettingsBinding

    private var intentComingFrom =  MainActivity::class.java.simpleName

    private var preferencesChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitle(R.string.settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val bundle = intent.extras
        intentComingFrom = bundle?.getString(USER_CLICKED_SETTINGS_FROM) ?: MainActivity::class.java.simpleName

        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        preferencesChanged = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //User might have come to settings from several places.
        // If they came from MainActivity, and if prefs have changed, MAinActivity should be recreated
        if (item.itemId == android.R.id.home) {
            if (preferencesChanged && intentComingFrom.equals(MainActivity::class.java.simpleName)) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}