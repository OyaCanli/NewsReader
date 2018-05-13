package com.example.oya.newsreader.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.data.NewsContract.BookmarkEntry;
import com.example.oya.newsreader.data.NewsContract.NewsEntry;
import com.example.oya.newsreader.model.NewsArticle;
import com.example.oya.newsreader.ui.SortSectionsActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class NewsDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "newsreader.db";

    private static final int DATABASE_VERSION = 1;

    private final Context mContext;

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

    public NewsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        Log.d("NewsDbHelper", "constructor of NEwsDbHelper is called");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create bookmarks table
        Log.d("NewsDbHelper", "OnCreate of the DBhelper is called");
        db.execSQL(SQL_CREATE_BOOKMARKS_TABLE);
        //Create tables for each section that are used by the user
        ArrayList<String> sectionList = SortSectionsActivity.getSections(mContext);
        for(String section: sectionList){
            db.execSQL(createSQLCommandWithTableName(section));
        }

    }

    public String createSQLCommandWithTableName(String sectionName){
        StringBuilder tableName = new StringBuilder();
        tableName.append("CREATE TABLE IF NOT EXISTS ")
                .append(sectionName).append(" (")
                .append(NewsEntry._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(NewsEntry.COLUMN_TITLE).append(" TEXT NOT NULL, ")
                .append(NewsEntry.COLUMN_THUMBNAIL_URL).append(" TEXT, ")
                .append(NewsEntry.COLUMN_AUTHOR).append(" TEXT, ")
                .append(NewsEntry.COLUMN_DATE).append(" TEXT, ")
                .append(NewsEntry.COLUMN_WEB_URL).append(" TEXT, ")
                .append(NewsEntry.COLUMN_SECTION).append(" TEXT, ")
                .append(NewsEntry.COLUMN_TRAIL).append(" TEXT, ")
                .append(NewsEntry.COLUMN_BODY).append(" TEXT);");
        return tableName.toString();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void clearCachedArticles(Context context){
        /*This method cleans unnecessary cached articles.
        If user changes section preferences, no need to save unchecked sections.*/
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
