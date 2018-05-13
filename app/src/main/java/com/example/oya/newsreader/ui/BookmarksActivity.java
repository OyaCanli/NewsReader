package com.example.oya.newsreader.ui;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.adapters.NewsCursorAdapter;
import com.example.oya.newsreader.data.NewsContract;
import com.example.oya.newsreader.data.NewsContract.BookmarkEntry;
import com.example.oya.newsreader.utils.Constants;

public class BookmarksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, NewsCursorAdapter.ListItemClickListener{

    private NewsCursorAdapter adapter;
    private RecyclerView recycler;
    private ProgressBar mLoadingIndicator;
    private TextView empty_tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ViewFlipper vf = findViewById(R.id.main_flipper);
        vf.setDisplayedChild(1);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(R.string.bookmark);
        mLoadingIndicator = findViewById(R.id.loading_indicator_search);
        empty_tv = findViewById(R.id.empty_view_search);
        recycler = findViewById(R.id.recycler_search);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setItemAnimator(new DefaultItemAnimator());
        adapter = new NewsCursorAdapter(this, this);
        recycler.setAdapter(adapter);
        showLoading();
        getSupportLoaderManager().initLoader(Constants.CURSOR_LOADER_ID, null, this);

        final CoordinatorLayout coordinator = findViewById(R.id.main_content);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                long id = (long) viewHolder.itemView.getTag();
                final ContentValues valuesOfArticleToErase = backUpArticleBeforeDeleting(id);
                removeBookmarkFromDatabase(id);

                Snackbar snackbar = Snackbar
                        .make(coordinator, R.string.snackbar_message_article_deleted, Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.undo), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addBackToDatabase(valuesOfArticleToErase);
                            }
                        });
                snackbar.show();
            }
        }).attachToRecyclerView(recycler);
    }

    private void showLoading() {
        /* Then, hide the weather data */
        recycler.setVisibility(View.GONE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showData() {
        /* Then, hide the weather data */
        recycler.setVisibility(View.VISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onListItemClick(View view, long id) {
        switch (view.getId()) {
            case R.id.container: {
                openDetails(id);
                break;
            }
            case R.id.share: {
                shareTheLink(id);
                break;
            }
            case R.id.bookmark: {
                Toast.makeText(this, "Article is already bookmarked. Swipe left or right to remove from bookmarks.", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    private Uri getUriById(long id){
        return ContentUris.withAppendedId(NewsContract.BookmarkEntry.CONTENT_URI, id);
    }

    private void openDetails(long id) {
        Uri uri = getUriById(id);
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.setData(uri);
        startActivity(intent);
    }

    private void shareTheLink(long id) {
        Uri uri = getUriById(id);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null );
        cursor.moveToFirst();
        String webUrl = cursor.getString(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_WEB_URL));
        cursor.close();
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, webUrl);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private ContentValues backUpArticleBeforeDeleting(long id){
        Uri uri = getUriById(id);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null );
        cursor.moveToFirst();
        ContentValues values = new ContentValues();
        values.put(BookmarkEntry.COLUMN_TITLE, cursor.getString(cursor.getColumnIndex(BookmarkEntry.COLUMN_TITLE)));
        values.put(BookmarkEntry.COLUMN_THUMBNAIL_URL, cursor.getString(cursor.getColumnIndex(BookmarkEntry.COLUMN_THUMBNAIL_URL)));
        values.put(BookmarkEntry.COLUMN_AUTHOR, cursor.getString(cursor.getColumnIndex(BookmarkEntry.COLUMN_AUTHOR)));
        values.put(BookmarkEntry.COLUMN_DATE, cursor.getString(cursor.getColumnIndex(BookmarkEntry.COLUMN_DATE)));
        values.put(BookmarkEntry.COLUMN_WEB_URL, cursor.getString(cursor.getColumnIndex(BookmarkEntry.COLUMN_WEB_URL)));
        values.put(BookmarkEntry.COLUMN_SECTION, cursor.getString(cursor.getColumnIndex(BookmarkEntry.COLUMN_SECTION)));
        values.put(BookmarkEntry.COLUMN_TRAIL, cursor.getString(cursor.getColumnIndex(BookmarkEntry.COLUMN_TRAIL)));
        values.put(BookmarkEntry.COLUMN_BODY, cursor.getString(cursor.getColumnIndex(BookmarkEntry.COLUMN_BODY)));
        cursor.close();
        return values;
    }

    private void removeBookmarkFromDatabase(long id){
        Uri uri = getUriById(id);
        getContentResolver().delete(uri, null, null);
    }

    private void addBackToDatabase(ContentValues values){
        getContentResolver().insert(NewsContract.BookmarkEntry.CONTENT_URI, values);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this,
                NewsContract.BookmarkEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mLoadingIndicator.setVisibility(View.GONE);
        adapter.swapCursor(data);
        if (data.getCount() != 0) {
            showData();
        } else {
            empty_tv.setText(R.string.no_bookmarks);
            recycler.setVisibility(View.GONE);
            empty_tv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bookmarks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_settings:{
                Intent intent = new Intent(BookmarksActivity.this, SettingsActivity.class);
                intent.putExtra(Constants.USER_CLICKED_SETTINGS_FROM, BookmarksActivity.class.getSimpleName());
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
