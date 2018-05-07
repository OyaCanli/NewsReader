package com.example.oya.newsreader.ui;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.adapters.NewsAdapter;
import com.example.oya.newsreader.data.NewsContract;
import com.example.oya.newsreader.data.NewsDbHelper;
import com.example.oya.newsreader.model.NewsArticle;
import com.example.oya.newsreader.utils.Constants;

import java.util.ArrayList;

public class BookmarksActivity extends AppCompatActivity implements NewsAdapter.ListItemClickListener{

    ArrayList<NewsArticle> bookmarkedArticles;
    NewsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        Toolbar toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RecyclerView recycler = findViewById(R.id.recycler_search);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setItemAnimator(new DefaultItemAnimator());
        NewsDbHelper dbHelper = new NewsDbHelper(this, null);
        bookmarkedArticles = dbHelper.getBookmarks();
        if(bookmarkedArticles.isEmpty()){
            TextView empty_tv = findViewById(R.id.empty_view_bookmarks);
            recycler.setVisibility(View.GONE);
            empty_tv.setVisibility(View.VISIBLE);
        } else{
            adapter = new NewsAdapter(this, bookmarkedArticles, this);
            recycler.setAdapter(adapter);
        }
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getLayoutPosition();
                Log.d("BookmarksActivity", "position is: "+ position);
                removeBookmark(position);
            }
        }).attachToRecyclerView(recycler);
    }

    @Override
    public void onListItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.container: {
                openDetails(position);
                break;
            }
            case R.id.share: {
                shareTheLink(position);
                break;
            }
            case R.id.bookmark: {
                Toast.makeText(this, "Article is already bookmarked. Swipe left or right to remove from bookmarks.", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    private void openDetails(int position) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(Constants.CHOSEN_ARTICLE, bookmarkedArticles.get(position));
        startActivity(intent);
    }

    private void shareTheLink(int position) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, bookmarkedArticles.get(position).getWebUrl());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void removeBookmark(int position){
        NewsDbHelper dbHelper = new NewsDbHelper(this, null);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = bookmarkedArticles.get(position).getArticleId();
        db.delete(NewsContract.BookmarkEntry.TABLE_NAME, NewsContract.BookmarkEntry._ID + "=" + id, null);
        bookmarkedArticles.remove(position);
        adapter.notifyItemRemoved(position);

    }
}
