package com.example.oya.newsreader.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.oya.newsreader.data.NewsContract.NewsEntry;
import com.example.oya.newsreader.data.NewsContract.BookmarkEntry;

public class NewsProvider extends ContentProvider {

    private static final String LOG_TAG = NewsProvider.class.getSimpleName();

    private static final int SECTION = 100;
    private static final int SECTION_WITH_ID = 101;
    private static final int BOOKMARKS = 200;
    private static final int BOOKMARKS_WITH_ID = 201;

    private NewsDbHelper mDbHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDbHelper = new NewsDbHelper(context);
        Log.d("NewsProvider", "onCreate of NewsProvider is called");

        return true;
    }

    static {
        sUriMatcher.addURI(NewsContract.AUTHORITY, NewsContract.PATH_BOOKMARKS, BOOKMARKS);
        sUriMatcher.addURI(NewsContract.AUTHORITY, NewsContract.PATH_BOOKMARKS + "/#", BOOKMARKS_WITH_ID);
        sUriMatcher.addURI(NewsContract.AUTHORITY, "*", SECTION);
        sUriMatcher.addURI(NewsContract.AUTHORITY, "*/#", SECTION_WITH_ID);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor = null;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKMARKS: {
                // Get all Bookmarks
                cursor = database.query(BookmarkEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, BookmarkEntry.COLUMN_DATE);
                break;
            }
            case BOOKMARKS_WITH_ID: {
                // Get one row from the table
                selection = BookmarkEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BookmarkEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            }
            case SECTION: {
                // Get all articles of a section
                String tableName = uri.getLastPathSegment();
                try{
                    cursor = database.query(tableName, projection, selection, selectionArgs,
                            null, null, sortOrder);
                } catch(SQLiteException e){
                    Log.e(LOG_TAG, e.toString());
                }

                break;
            }
            case SECTION_WITH_ID: {
                // Get one row from the table
                String tableNameWithId = uri.toString().substring(37);
                String[] parts = tableNameWithId.split("/");
                selection = NewsEntry._ID + "=?";
                selectionArgs = new String[]{parts[1]};
                cursor = database.query(parts[0], projection, selection, selectionArgs,
                        null, null, sortOrder);
                Log.d("NewsProvider", "" + cursor.getCount());
                break;
            }
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        if(cursor != null) cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKMARKS:
                return BookmarkEntry.CONTENT_LIST_TYPE;
            case BOOKMARKS_WITH_ID:
                return BookmarkEntry.CONTENT_ITEM_TYPE;
            case SECTION:
                return NewsEntry.CONTENT_LIST_TYPE;
            case SECTION_WITH_ID:
                return NewsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        Uri returnUri;
        switch (match) {
            case BOOKMARKS:
            case SECTION_WITH_ID:{
                long id = db.insert(BookmarkEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(BookmarkEntry.CONTENT_URI, id);
                } else {
                    throw new SQLiteException("Failed to insert");
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String tableName = uri.getLastPathSegment();

        int rowsInserted = 0;
        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                long id = db.insert(tableName, null, value);
                if (id != -1) rowsInserted++;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        if (rowsInserted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsInserted;
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SECTION:
                String tableName = uri.getLastPathSegment();
                database.execSQL(mDbHelper.createSQLCommandWithTableName(tableName));
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(tableName, selection, selectionArgs);
                break;
            case BOOKMARKS_WITH_ID:
                // Delete a single row given by the ID in the URI
                selection = BookmarkEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookmarkEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
