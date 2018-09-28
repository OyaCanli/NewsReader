package com.example.oya.newsreader.utils;

import com.example.oya.newsreader.BuildConfig;

public final class Constants {

    //Constants related to API
    static final String BASE_URL = "http://content.guardianapis.com/search?";
    static final String SECTION_PARAM = "section";
    static final String SHOW_FIELDS_KEY = "show-fields";
    static final String SHOW_FIELDS_VALUE = "byline,trailText,thumbnail,body";
    static final String ORDER_BY_PARAM = "order-by";
    static final String PAGE_SIZE_PARAM = "page-size";
    static final String FROM_DATE = "from-date";
    static final String GUARDIAN_API_KEY = "api-key";
    static final String GUARDIAN_API_VALUE = BuildConfig.ApiKey;
    static final String RESPONSE = "response";
    static final String RESULTS = "results";
    static final String WEB_TITLE = "webTitle";
    static final String AUTHOR_NAME = "byline";
    static final String TRAIL = "trailText";
    static final String BODY = "body";
    static final String SECTION = "sectionName";
    static final String DATE_AND_TIME = "webPublicationDate";
    static final String WEB_URL = "webUrl";
    static final String FIELDS = "fields";
    static final String THUMBNAIL = "thumbnail";

    //Constants used in intent extras
    public static final String CHOSEN_ARTICLE = "chosenArticle";
    public static final String USER_CLICKED_SETTINGS_FROM = "userClickedSettingsFrom";
    public static final String SEARCH_QUERY = "searchQuery";
    public static final String IS_PREFERENCES_CHANGED = "isPreferencesChanged";

    //Constants for ids
    public static final int SEARCH_LOADER_ID = 1013;
    public static final int CURSOR_LOADER_ID = 207;

    public final static String SCROLL_X = "scrollX";
    public final static String SCROLL_Y = "scrollY";

    public static final String BROADCAST_ACTION = "com.example.oya.newsreader.SYNC_FINISHED";
}
