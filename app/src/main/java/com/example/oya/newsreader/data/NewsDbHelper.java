package com.example.oya.newsreader.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.oya.newsreader.data.NewsContract.*;

public class NewsDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = NewsDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "newsreader.db";

    private static final int DATABASE_VERSION = 1;

    public NewsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
// Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_NEWS_TABLE =  "CREATE TABLE " + NewsEntry.TABLE_NAME + " ("
                + NewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NewsEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + NewsEntry.COLUMN_THUMBNAIL_URL + " TEXT, "
                + NewsEntry.COLUMN_AUTHOR + " TEXT, "
                + NewsEntry.COLUMN_DATE + " TEXT, "
                + NewsEntry.COLUMN_SECTION + " TEXT, "
                + NewsEntry.COLUMN_TRAIL + " TEXT, "
                + NewsEntry.COLUMN_BODY + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_NEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
