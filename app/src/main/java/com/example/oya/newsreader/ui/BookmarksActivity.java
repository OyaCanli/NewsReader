package com.example.oya.newsreader.ui;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.adapters.NewsAdapter;
import com.example.oya.newsreader.data.NewsContract;
import com.example.oya.newsreader.data.NewsDbHelper;
import com.example.oya.newsreader.model.NewsArticle;
import com.example.oya.newsreader.utils.Constants;

import java.util.ArrayList;
import java.util.LinkedList;

public class BookmarksActivity extends AppCompatActivity implements NewsAdapter.ListItemClickListener{

    private ArrayList<NewsArticle> bookmarkedArticles;
    private NewsAdapter adapter;
    private final LinkedList<NewsArticle> tempListOfArticlesToErase = new LinkedList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ViewFlipper vf = findViewById(R.id.main_flipper);
        vf.setDisplayedChild(4);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.bookmark);
        RecyclerView recycler = findViewById(R.id.recycler_bookmarks);
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
        final CoordinatorLayout coordinator = findViewById(R.id.main_content);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getLayoutPosition();
                final NewsArticle articleToErase = bookmarkedArticles.get(position);
                removeBookmarkFromAdapter(position);
                tempListOfArticlesToErase.add(articleToErase);
                Snackbar snackbar = Snackbar
                        .make(coordinator, R.string.snackbar_message_article_deleted, Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.undo), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                bookmarkedArticles.add(position, articleToErase);
                                adapter.notifyItemInserted(position);
                                tempListOfArticlesToErase.removeLast();
                            }
                        });
                snackbar.show();
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

    private void removeBookmarkFromAdapter(int position){
        bookmarkedArticles.remove(position);
        adapter.notifyItemRemoved(position);
    }

    private void removeDeletedBookmarksFromDatabase(){
        NewsDbHelper dbHelper = new NewsDbHelper(this, null);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for(NewsArticle articleToErase : tempListOfArticlesToErase){
            long id = articleToErase.getArticleId();
            db.delete(NewsContract.BookmarkEntry.TABLE_NAME, NewsContract.BookmarkEntry._ID + "=" + id, null);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeDeletedBookmarksFromDatabase();
    }
}
