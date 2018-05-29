package com.example.oya.newsreader.utils;

import com.example.oya.newsreader.BuildConfig;

public final class Constants {

    //Constants related to API
    public static final String BASE_URL = "http://content.guardianapis.com/search?";
    public static final String SECTION_PARAM = "section";
    public static final String SHOW_FIELDS_KEY = "show-fields";
    public static final String SHOW_FIELDS_VALUE = "byline,trailText,thumbnail,body";
    public static final String ORDER_BY_PARAM = "order-by";
    public static final String PAGE_SIZE_PARAM = "page-size";
    public static final String FROM_DATE = "from-date";
    public static final String GUARDIAN_API_KEY = "api-key";
    public static final String GUARDIAN_API_VALUE = BuildConfig.ApiKey;
    public static final String RESPONSE = "response";
    public static final String RESULTS = "results";
    public static final String WEB_TITLE = "webTitle";
    public static final String AUTHOR_NAME = "byline";
    public static final String TRAIL = "trailText";
    public static final String BODY = "body";
    public static final String SECTION = "sectionName";
    public static final String DATE_AND_TIME = "webPublicationDate";
    public static final String WEB_URL = "webUrl";
    public static final String FIELDS = "fields";
    public static final String THUMBNAIL = "thumbnail";

    //Constants used in intent extras
    public static final String CHOSEN_ARTICLE = "chosenArticle";
    public static final String USER_CLICKED_SETTINGS_FROM = "userClickedSettingsFrom";
    public static final String SEARCH_QUERY = "searchQuery";
    public static final String IS_PREFERENCES_CHANGED = "isPreferencesChanged";

    //Constants for ids
    public static final int SEARCH_LOADER_ID = 1013;
    public static final int CURSOR_LOADER_ID = 207;
}
