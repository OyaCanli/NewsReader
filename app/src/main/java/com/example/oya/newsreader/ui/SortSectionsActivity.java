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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

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
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sectionList);
        dragSortListView.setAdapter(adapter);
        dragSortListView.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                String movedItem = sectionList.get(from);
                sectionList.remove(from);
                if (from > to) --from;
                sectionList.add(to, movedItem);
                adapter.notifyDataSetChanged();
                for(int i=0; i< sectionList.size() ; i++){
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

    private void updatePreferences(){
        for(int i=0; i< sectionList.size() ; i++){
            Log.d("SortSectionsActivity", "before saving to prefs" + sectionList.get(i));
        }
        LinkedHashSet<String> sortedSectionSet = new LinkedHashSet<>(sectionList);
        Iterator<String> iterator = sortedSectionSet.iterator();
        while(iterator.hasNext()) {
            Log.d("LinkedHashSet", "" + iterator.next());
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.PREF_SORTED_SECTIONS);
        editor.putStringSet(Constants.PREF_SORTED_SECTIONS, sortedSectionSet);
        editor.apply();
        /////This part is for testing///////
        LinkedHashSet<String> default_sections = new LinkedHashSet<>(Arrays.asList(getResources().getStringArray(R.array.pref_section_default_values)));
        Set<String> orderedSections = sharedPreferences.getStringSet(Constants.PREF_SORTED_SECTIONS, default_sections);
        ArrayList<String> sections = new ArrayList<>(orderedSections);
        for(int i=0; i< sections.size() ; i++){
            Log.d("SortSectionsActivity", "retrieved back from preferences" + sections.get(i));
        }
    }

    private ArrayList<String> getSections(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> default_sections = new HashSet<>(Arrays.asList(getResources().getStringArray(R.array.pref_section_default_values)));
        Set<String> orderedSections = sharedPreferences.getStringSet(Constants.PREF_SORTED_SECTIONS, default_sections);
        return new ArrayList<>(orderedSections);
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
