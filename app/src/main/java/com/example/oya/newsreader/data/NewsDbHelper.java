package com.example.oya.newsreader.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.data.NewsContract.NewsEntry;
import com.example.oya.newsreader.data.NewsContract.BookmarkEntry;
import com.example.oya.newsreader.model.NewsArticle;
import com.example.oya.newsreader.ui.SortSectionsActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewsDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "newsreader.db";

    private static final int DATABASE_VERSION = 1;

    private final String mSection;

    private final String SQL_CREATE_NEWS_TABLE;
    private final String SQL_CREATE_BOOKMARKS_TABLE = "CREATE TABLE IF NOT EXISTS " + BookmarkEntry.TABLE_NAME + " ("
            + BookmarkEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BookmarkEntry.COLUMN_TITLE + " TEXT NOT NULL, "
            + BookmarkEntry.COLUMN_THUMBNAIL_URL + " TEXT, "
            + BookmarkEntry.COLUMN_AUTHOR + " TEXT, "
            + BookmarkEntry.COLUMN_DATE + " TEXT, "
            + BookmarkEntry.COLUMN_WEB_URL + " TEXT, "
            + BookmarkEntry.COLUMN_SECTION + " TEXT, "
            + BookmarkEntry.COLUMN_TRAIL + " TEXT, "
            + BookmarkEntry.COLUMN_BODY + " TEXT);";

    public NewsDbHelper(Context context, String section) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mSection = section;
        SQL_CREATE_NEWS_TABLE = "CREATE TABLE IF NOT EXISTS " + mSection + " ("
                + NewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NewsEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + NewsEntry.COLUMN_THUMBNAIL_URL + " TEXT, "
                + NewsEntry.COLUMN_AUTHOR + " TEXT, "
                + NewsEntry.COLUMN_DATE + " TEXT, "
                + NewsEntry.COLUMN_WEB_URL + " TEXT, "
                + NewsEntry.COLUMN_SECTION + " TEXT, "
                + NewsEntry.COLUMN_TRAIL + " TEXT, "
                + NewsEntry.COLUMN_BODY + " TEXT);";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
// Create a String that contains the SQL statement to create the pets table
        Log.d("NewsDbHelper", "OnCreate of the DBhelper is called");
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_NEWS_TABLE);
        db.execSQL(SQL_CREATE_BOOKMARKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    synchronized public void backUpToDatabase(final List<NewsArticle> list) {
        AsyncTask mTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                SQLiteDatabase db = getWritableDatabase();
                db.execSQL(SQL_CREATE_NEWS_TABLE);
                db.delete(mSection, null, null);
                for(NewsArticle article : list){
                    ContentValues values = new ContentValues();
                    values.put(NewsEntry.COLUMN_TITLE, article.getTitle());
                    values.put(NewsEntry.COLUMN_THUMBNAIL_URL, article.getThumbnailUrl());
                    values.put(NewsEntry.COLUMN_AUTHOR, article.getAuthor());
                    values.put(NewsEntry.COLUMN_DATE, article.getDate());
                    values.put(NewsEntry.COLUMN_WEB_URL, article.getWebUrl());
                    values.put(NewsEntry.COLUMN_SECTION, article.getSection());
                    values.put(NewsEntry.COLUMN_TRAIL, article.getArticleTrail());
                    values.put(NewsEntry.COLUMN_BODY, article.getArticleBody());
                    long newRowId = db.insert(mSection, null, values);
                    Log.d("NewsDbHelper", "newRowId" + newRowId);
                }
                db.close();
                return null;
            }
        };
        mTask.execute();
    }

    public ArrayList<NewsArticle> readFromDatabase(String section) {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL(SQL_CREATE_NEWS_TABLE);
        Cursor cursor = db.query(
                section,   // The table to query
                null,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        // Find the columns of news attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(NewsEntry._ID);
        int titleColumnIndex = cursor.getColumnIndex(NewsEntry.COLUMN_TITLE);
        int thumbnailColumnIndex = cursor.getColumnIndex(NewsEntry.COLUMN_THUMBNAIL_URL);
        int authorColumnIndex = cursor.getColumnIndex(NewsEntry.COLUMN_AUTHOR);
        int dateColumnIndex = cursor.getColumnIndex(NewsEntry.COLUMN_DATE);
        int webUrlColumnIndex = cursor.getColumnIndex(NewsEntry.COLUMN_WEB_URL);
        int sectionColumnIndex = cursor.getColumnIndex(NewsEntry.COLUMN_SECTION);
        int trailColumnIndex = cursor.getColumnIndex(NewsEntry.COLUMN_TRAIL);
        int bodyColumnIndex = cursor.getColumnIndex(NewsEntry.COLUMN_BODY);

        ArrayList<NewsArticle> newsList = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            newsList.add(new NewsArticle(cursor.getLong(idColumnIndex), cursor.getString(titleColumnIndex), cursor.getString(thumbnailColumnIndex),
                    cursor.getString(authorColumnIndex), cursor.getString(dateColumnIndex), cursor.getString(webUrlColumnIndex),
                    cursor.getString(sectionColumnIndex), cursor.getString(trailColumnIndex), cursor.getString(bodyColumnIndex)));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return newsList;
    }

    public void addToBookmarks(NewsArticle article) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(SQL_CREATE_BOOKMARKS_TABLE);
        ContentValues values = new ContentValues();
        values.put(BookmarkEntry.COLUMN_TITLE, article.getTitle());
        values.put(BookmarkEntry.COLUMN_THUMBNAIL_URL, article.getThumbnailUrl());
        values.put(BookmarkEntry.COLUMN_AUTHOR, article.getAuthor());
        values.put(BookmarkEntry.COLUMN_DATE, article.getDate());
        values.put(BookmarkEntry.COLUMN_WEB_URL, article.getWebUrl());
        values.put(BookmarkEntry.COLUMN_SECTION, article.getSection());
        values.put(BookmarkEntry.COLUMN_TRAIL, article.getArticleTrail());
        values.put(BookmarkEntry.COLUMN_BODY, article.getArticleBody());
        long newRowId = db.insert(BookmarkEntry.TABLE_NAME, null, values);
        db.close();
        Log.d("NewsDbHelper", "newRowId" + newRowId);
    }

    public ArrayList<NewsArticle> getBookmarks() {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL(SQL_CREATE_BOOKMARKS_TABLE);
        Cursor cursor = db.query(
                BookmarkEntry.TABLE_NAME,   // The table to query
                null,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        // Find the columns of news attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(NewsEntry._ID);
        int titleColumnIndex = cursor.getColumnIndex(BookmarkEntry.COLUMN_TITLE);
        int thumbnailColumnIndex = cursor.getColumnIndex(BookmarkEntry.COLUMN_THUMBNAIL_URL);
        int authorColumnIndex = cursor.getColumnIndex(BookmarkEntry.COLUMN_AUTHOR);
        int dateColumnIndex = cursor.getColumnIndex(BookmarkEntry.COLUMN_DATE);
        int webUrlColumnIndex = cursor.getColumnIndex(BookmarkEntry.COLUMN_WEB_URL);
        int sectionColumnIndex = cursor.getColumnIndex(BookmarkEntry.COLUMN_SECTION);
        int trailColumnIndex = cursor.getColumnIndex(BookmarkEntry.COLUMN_TRAIL);
        int bodyColumnIndex = cursor.getColumnIndex(BookmarkEntry.COLUMN_BODY);

        ArrayList<NewsArticle> list = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(new NewsArticle(cursor.getLong(idColumnIndex), cursor.getString(titleColumnIndex), cursor.getString(thumbnailColumnIndex),
                    cursor.getString(authorColumnIndex), cursor.getString(dateColumnIndex), cursor.getString(webUrlColumnIndex),
                    cursor.getString(sectionColumnIndex), cursor.getString(trailColumnIndex), cursor.getString(bodyColumnIndex)));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return list;
    }

    public void clearCachedArticles(Context context){
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> allSectionsAvailable = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.pref_section_values)));
        ArrayList<String> sectionUsed = SortSectionsActivity.getSections(context);
        allSectionsAvailable.removeAll(sectionUsed); //Now it contains only unused sections
        for(int i = 0; i < allSectionsAvailable.size(); ++i){
           String sql_drop_table =   "drop table if exists " + allSectionsAvailable.get(i);
            db.execSQL(sql_drop_table);
        }
        db.close();
    }
}
