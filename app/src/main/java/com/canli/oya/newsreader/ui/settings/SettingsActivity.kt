package com.canli.oya.newsreader.ui.settings

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import android.content.Intent
import android.view.MenuItem
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.canli.oya.newsreader.R
import com.canli.oya.newsreader.common.USER_CLICKED_SETTINGS_FROM
import com.canli.oya.newsreader.databinding.ActivitySettingsBinding
import com.canli.oya.newsreader.ui.main.MainActivity


@AndroidEntryPoint
class SettingsActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var binding : ActivitySettingsBinding

    private var preferencesChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitle(R.string.settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        // Instantiate the new Fragment
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader,
            pref.fragment
        )
        fragment.arguments = args
        fragment.setTargetFragment(caller, 0)
        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings_fragment, fragment)
            .addToBackStack(null)
            .commit()

        title = pref.title
        return true
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        preferencesChanged = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //User might have come to settings from several places.
        // If they came from MainActivity, and if prefs have changed, MAinActivity should be recreated
        if (item.itemId == android.R.id.home) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}