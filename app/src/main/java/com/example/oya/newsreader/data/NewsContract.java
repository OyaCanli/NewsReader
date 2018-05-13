package com.example.oya.newsreader.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class NewsContract {

    public static final String AUTHORITY = "com.example.oya.newsreader";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_BOOKMARKS = "bookmarks";

    //Empty private constructor so prevent this class from being instantiated
    private NewsContract() {
    }

    public static final class NewsEntry implements BaseColumns {


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/*";

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/*";


        //Unique id for each article
        public final static String _ID = BaseColumns._ID;

        //Title for each article. Type: Text
        public static final String COLUMN_TITLE = "title";

        //Url for thumbnail image of the article. Type: TEXT
        public static final String COLUMN_THUMBNAIL_URL = "thumbnailUrl";

        //Author of the article. Type: Text
        public static final String COLUMN_AUTHOR = "author";

        //Date and time of the article. Type: Text
        public static final String COLUMN_DATE = "date";

        //Web url of the article. Type: Text
        public static final String COLUMN_WEB_URL = "webUrl";

        //Section of the article. Type: Text
        public static final String COLUMN_SECTION= "section";

        //Trail text(short description) of the article. Type: TEXT
        public static final String COLUMN_TRAIL = "trail";

        //Body of the article. Type: Text
        public static final String COLUMN_BODY = "body";
    }

    public static final class BookmarkEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_BOOKMARKS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_BOOKMARKS;

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOOKMARKS).build();

        //Table where news will temporarily cached for offline reading
        public static final String TABLE_NAME = "bookmarks";

        //Unique id for each article
        public final static String _ID = BaseColumns._ID;

        //Title for each article. Type: Text
        public static final String COLUMN_TITLE = "title";

        //Url for thumbnail image of the article. Type: TEXT
        public static final String COLUMN_THUMBNAIL_URL = "thumbnailUrl";

        //Author of the article. Type: Text
        public static final String COLUMN_AUTHOR = "author";

        //Date and time of the article. Type: Text
        public static final String COLUMN_DATE = "date";

        //Web url of the article. Type: Text
        public static final String COLUMN_WEB_URL = "webUrl";

        //Section of the article. Type: Text
        public static final String COLUMN_SECTION= "section";

        //Trail text(short description) of the article. Type: TEXT
        public static final String COLUMN_TRAIL = "trail";

        //Body of the article. Type: Text
        public static final String COLUMN_BODY = "body";
    }
}
