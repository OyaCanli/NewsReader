package com.example.oya.newsreader.data;

import android.provider.BaseColumns;

public class NewsContract {

    //Empty private constructor so prevent this class from being instantiated
    private NewsContract() {
    }

    public static final class NewsEntry implements BaseColumns {

        //Table where news will temporarily cached for offline reading
        public static final String TABLE_NAME = "cached_news";

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

        //Section of the article. Type: Text
        public static final String COLUMN_SECTION= "section";

        //Trail text(short description) of the article. Type: TEXT
        public static final String COLUMN_TRAIL = "trail";

        //Body of the article. Type: Text
        public static final String COLUMN_BODY = "body";


    }
}
