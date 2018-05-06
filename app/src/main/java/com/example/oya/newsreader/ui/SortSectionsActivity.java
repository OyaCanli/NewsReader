package com.example.oya.newsreader.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.utils.Constants;
import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;
import java.util.Arrays;

public class SortSectionsActivity extends AppCompatActivity {

    ArrayList<String> sectionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ViewFlipper vf = findViewById(R.id.main_flipper);
        vf.setDisplayedChild(3);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.sort_sections);
        sectionList = getSections();
        DragSortListView dragSortListView = findViewById(R.id.list);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.dragsort_list_item, sectionList);
        dragSortListView.setAdapter(adapter);
        dragSortListView.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                String movedItem = sectionList.get(from);
                sectionList.remove(from);
                if (from > to) --from;
                sectionList.add(to, movedItem);
                adapter.notifyDataSetChanged();
                for (int i = 0; i < sectionList.size(); i++) {
                    Log.d("DropListener", "" + sectionList.get(i));
                }
            }
        });
        Toast.makeText(this, getString(R.string.toast_message_sort_sections), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        updatePreferences();
    }

    private void updatePreferences() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("sections_size", sectionList.size());
        for (int i = 0; i < sectionList.size(); i++) {
            editor.remove("section_" + i);
            editor.putString("section_" + i, sectionList.get(i));
        }
        editor.apply();
    }

    private ArrayList<String> getSections() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ArrayList<String> preferredSections = new ArrayList<>();
        int size = sharedPreferences.getInt("sections_size", 0);

        for (int i = 0; i < size; i++) {
            preferredSections.add(sharedPreferences.getString("section_" + i, null));
        }

        for (int i = 0; i < size; ++i) {
            Log.d("SortSections", "'after retrieving arraylist" + preferredSections.get(i));
        }
        if (preferredSections.isEmpty()) {
            ArrayList<String> default_sections = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.pref_section_default_values)));
            preferredSections.addAll(default_sections);
        }
        return preferredSections;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra(Constants.USER_CLICKED_SETTINGS_FROM, SortSectionsActivity.class.getSimpleName());
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
