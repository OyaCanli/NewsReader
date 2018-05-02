package com.example.oya.newsreader.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.model.NewsArticle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public final class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    private NetworkUtils(){
        //Make it impossible to instantiate
        throw new AssertionError();
    }

    public static List<NewsArticle> fetchArticles(String section, Context context) {
        // Create URL object
        URL url = null;
        try {
            url = buildUrl(section, context);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        List<NewsArticle> articles = extractFeatureFromJson(jsonResponse);
        /*if(articles != null){
            // Return the list of {@link Earthquake}s
            for(int i = 0; i<articles.size(); ++i){
                Log.v(LOG_TAG, "" + articles.get(i).toString());
            }
        }*/
        return articles;
    }

    public static List<NewsArticle> searchOnline(String section) {
        // Create URL object
        URL url = buildSearchUrl(section);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        // Extract relevant fields from the JSON response and create a list of {@link NewsArticle}s
        List<NewsArticle> articles = extractFeatureFromJson(jsonResponse);
        if(articles != null){
            // Return the list of {@link Earthquake}s
            for(int i = 0; i<articles.size(); ++i){
                Log.v(LOG_TAG, "" + articles.get(i).toString());
            }
        }
        return articles;
    }

    public static List<NewsArticle> checkForNewArticle(Context context) {
        // Create URL object
        URL url = buildUrlForNotification(context);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        List<NewsArticle> articles = extractFeatureFromJson(jsonResponse);
        if(articles != null){
            // Return the list of {@link Earthquake}s
            for(int i = 0; i<articles.size(); ++i){
                Log.v(LOG_TAG, "article is" + articles.get(i).toString());
            }
            Log.v(LOG_TAG, "articles size" + articles.size());
        }
        return articles;
    }

    private static URL buildSearchUrl(String query){
        Uri uri = Uri.parse(Constants.BASE_URL).buildUpon()
                .appendQueryParameter("q", query)
                .appendQueryParameter(Constants.SHOW_FIELDS_KEY, Constants.SHOW_FIELDS_VALUE)
                .appendQueryParameter(Constants.ORDER_BY_PARAM, "relevance")
                .appendQueryParameter(Constants.PAGE_SIZE_PARAM, "25")
                .appendQueryParameter(Constants.GUARDIAN_API_KEY, Constants.GUARDIAN_API_VALUE )
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static URL buildUrlForNotification(Context context){

        Uri builtUri = Uri.parse(Constants.BASE_URL).buildUpon()
                .encodedQuery(Constants.FROM_DATE + "=" + getFormattedDateTime())
                .appendQueryParameter(Constants.SECTION_PARAM, context.getString(R.string.politics).toLowerCase())
                .appendQueryParameter(Constants.SHOW_FIELDS_KEY, Constants.SHOW_FIELDS_VALUE)
                .appendQueryParameter(Constants.ORDER_BY_PARAM, context.getString(R.string.pref_orderby_default))
                .appendQueryParameter(Constants.PAGE_SIZE_PARAM, "1")
                .appendQueryParameter(Constants.GUARDIAN_API_KEY, Constants.GUARDIAN_API_VALUE )
                .build();

        /*StringBuilder builtUri = new StringBuilder();
        builtUri.append(Constants.BASE_URL)
                .append(Constants.SECTION_PARAM)
                .append("=")
                .append(context.getString(R.string.politics).toLowerCase())
                .append("&")
                .append(Constants.FROM_DATE)
                .append("=")
                .append(getFormattedDateTime())
                .append("&")
                .append(Constants.SHOW_FIELDS_KEY)
                .append("=")
                .append(Constants.SHOW_FIELDS_VALUE)
                .append("&")
                .append(Constants.ORDER_BY_PARAM)
                .append("=")
                .append(context.getString(R.string.pref_orderby_default))
                .append("&")
                .append(Constants.PAGE_SIZE_PARAM)
                .append("=1&")
                .append(Constants.GUARDIAN_API_KEY)
                .append("=")
                .append(Constants.GUARDIAN_API_VALUE);*/

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String getFormattedDateTime(){
        String currentDateTime = getFormattedSystemDate() + "T" + getFormattedSystemTime() + "Z";
        return currentDateTime;
    }

    private static String getFormattedSystemDate() {
        Date currentDate= Calendar.getInstance().getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(currentDate);
    }

    private static String getFormattedSystemTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        long now = Calendar.getInstance().getTimeInMillis();
        return dateFormat.format(now-5400000);
        //TODO: this should be localized. Times on api are time of UK. User can be anywhere.
    }

    /*private static String buildSectionsParam(){
        StringBuilder sectionsParam = new StringBuilder();
        ArrayList<String> sections = SectionsPagerAdapter.getSections();
        int size = sections.size();
        for(int i=0; i < size-1 ; i++){
            sectionsParam.append(sections.get(i));
            sectionsParam.append(",");
        }
        sectionsParam.append(sections.get(size-1));
        return sectionsParam.toString();
    }*/

    private static URL buildUrl(String section, Context context) throws UnsupportedEncodingException {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Uri builtUri = Uri.parse(Constants.BASE_URL).buildUpon()
                .appendQueryParameter(Constants.SECTION_PARAM, section)
                .appendQueryParameter(Constants.SHOW_FIELDS_KEY, Constants.SHOW_FIELDS_VALUE)
                .appendQueryParameter(Constants.ORDER_BY_PARAM, sharedPreferences.getString(context.getString(R.string.pref_key_orderBy), context.getString(R.string.pref_orderby_default)))
                .appendQueryParameter(Constants.PAGE_SIZE_PARAM, sharedPreferences.getString(context.getString(R.string.pref_key_itemsPerPage), context.getString(R.string.pref_itemPerPage_default)))
                .appendQueryParameter(Constants.GUARDIAN_API_KEY, Constants.GUARDIAN_API_VALUE )
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }
    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<NewsArticle> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<NewsArticle> articleList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            JSONObject response = baseJsonResponse.getJSONObject(Constants.RESPONSE);

            JSONArray resultsArray = response.getJSONArray(Constants.RESULTS);

            // For each earthquake in the earthquakeArray, create an {@link Earthquake} object
            for (int i = 0; i < resultsArray.length(); i++) {

                // Get a single earthquake at position i within the list of earthquakes
                JSONObject currentArticle = resultsArray.getJSONObject(i);

                String title = currentArticle.getString(Constants.WEB_TITLE);
                String articleUrl = currentArticle.getString(Constants.WEB_URL);
                String date = currentArticle.optString(Constants.DATE_AND_TIME, "");
                String section = currentArticle.getString(Constants.SECTION);
                JSONObject fields = currentArticle.getJSONObject(Constants.FIELDS);
                String author = fields.optString(Constants.AUTHOR_NAME, " ");
                String trail = fields.optString(Constants.TRAIL,"");
                String body = fields.optString(Constants.BODY, " ");
                String thumbnail = fields.optString(Constants.THUMBNAIL, "");

                // Add the new {@link NewsArticle} to the list of articles.
                articleList.add(new NewsArticle(title, thumbnail, author, date, articleUrl, section, trail, body));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        return articleList;
    }

}
