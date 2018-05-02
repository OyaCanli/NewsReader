package com.example.oya.newsreader.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.oya.newsreader.data.NewsContract.NewsEntry;
import com.example.oya.newsreader.model.NewsArticle;

import java.util.ArrayList;
import java.util.List;

public class NewsDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = NewsDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "newsreader.db";

    private static final int DATABASE_VERSION = 1;

    private String mSection;

    String SQL_CREATE_NEWS_TABLE;

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void backUpToDatabase(List<NewsArticle> list) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(SQL_CREATE_NEWS_TABLE);
        if(!tableEmpty(mSection)) db.delete(mSection, null, null);
        for (int i = 0; i < list.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(NewsEntry.COLUMN_TITLE, list.get(i).getTitle());
            values.put(NewsEntry.COLUMN_THUMBNAIL_URL, list.get(i).getThumbnailUrl());
            values.put(NewsEntry.COLUMN_AUTHOR, list.get(i).getAuthor());
            values.put(NewsEntry.COLUMN_DATE, list.get(i).getDate());
            values.put(NewsEntry.COLUMN_WEB_URL, list.get(i).getWebUrl());
            values.put(NewsEntry.COLUMN_SECTION, list.get(i).getSection());
            values.put(NewsEntry.COLUMN_TRAIL, list.get(i).getArticleTrail());
            values.put(NewsEntry.COLUMN_BODY, list.get(i).getArticleBody());
            long newRowId = db.insert(mSection, null, values);
            Log.d("NewsDbHelper", "newRowId" + newRowId);
        }
    }

    private boolean tableEmpty(String tableName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.close();
                    return false;
                }
                cursor.close();
            }
        return true;
    }

    public ArrayList<NewsArticle> readFromDatabase(String section){
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
        while(!cursor.isAfterLast()){
            newsList.add(new NewsArticle(cursor.getString(titleColumnIndex), cursor.getString(thumbnailColumnIndex),
                    cursor.getString(authorColumnIndex), cursor.getString(dateColumnIndex), cursor.getString(webUrlColumnIndex),
                    cursor.getString(sectionColumnIndex), cursor.getString(trailColumnIndex), cursor.getString(bodyColumnIndex)));
            cursor.moveToNext();
        }
        cursor.close();
        if(db != null) db.close();
        return newsList;
    }
}
