package com.example.oya.newsreader.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.adapters.SectionsPagerAdapter;
import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;

public class SortSectionsActivity extends AppCompatActivity {

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
        final ArrayList<String> sectionList = SectionsPagerAdapter.getSections();
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
            }
        });
        Toast.makeText(this, getString(R.string.toast_message_sort_sections), Toast.LENGTH_SHORT).show();
    }
}
