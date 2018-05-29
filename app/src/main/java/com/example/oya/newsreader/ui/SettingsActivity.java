package com.example.oya.newsreader.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ViewFlipper;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.utils.Constants;

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
        /*I implemented this here as well, because I needed this info(whether preferences
        are changed) in order to decide the behavior of my UP and BACK buttons.*/
        preferencesChanged = true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onBackPressed() {
        if(preferencesChanged && (intentComingFrom.equals(MainActivity.class.getSimpleName())
                || intentComingFrom.equals(SortSectionsActivity.class.getSimpleName()))){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constants.IS_PREFERENCES_CHANGED, preferencesChanged);
            startActivity(intent);
        }
        super.onBackPressed();
    }
}
