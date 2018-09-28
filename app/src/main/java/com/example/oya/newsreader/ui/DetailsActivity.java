package com.example.oya.newsreader.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.data.NewsContract;
import com.example.oya.newsreader.data.NewsContract.BookmarkEntry;
import com.example.oya.newsreader.model.NewsArticle;
import com.example.oya.newsreader.utils.Constants;
import com.example.oya.newsreader.utils.GlideApp;
import com.example.oya.newsreader.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DetailsActivity extends AppCompatActivity{

    private String webUrl = null;
    private String title = null;
    private String trail = null;
    private String body = null;
    private String imageUrl = null;
    private String author = null;
    private String section = null;
    private String time = null;
    private NestedScrollView scrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ViewFlipper vf = findViewById(R.id.main_flipper);
        vf.setDisplayedChild(0);
        //Set the toolbar and enable up button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Initialize views
        scrollView = findViewById(R.id.details_root_scroll);
        TextView title_tv = findViewById(R.id.details_title);
        TextView trail_tv = findViewById(R.id.details_trail);
        TextView body_tv = findViewById(R.id.details_body);
        ImageView details_iv = findViewById(R.id.details_image);
        TextView author_tv = findViewById(R.id.details_author);
        TextView section_tv = findViewById(R.id.details_section);
        TextView time_tv = findViewById(R.id.details_time);
        //Get the chosen article info from the bundle
        Intent intent = getIntent();
        //If user clicked an article from the lists in the mainActivity or from the bookmarks screen, There will be a url in the intent extra.
        Uri uri = intent.getData();
        if(uri != null){
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            title = cursor.getString(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_TITLE));
            trail = cursor.getString(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_TRAIL));
            body = cursor.getString(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_BODY));
            imageUrl = cursor.getString(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_THUMBNAIL_URL));
            author = cursor.getString(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_AUTHOR));
            section = cursor.getString(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_SECTION));
            time = cursor.getString(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_DATE));
            webUrl = cursor.getString(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_WEB_URL));
            cursor.close();
        } else{
            /*If user clicked an article from the search results, that article is not on the database.
            Similarly, if user clicked on a notification, that article is not on database.
            In that cases intent contains a parcelable NewsArticle object*/
            NewsArticle chosenArticle = intent.getParcelableExtra(Constants.CHOSEN_ARTICLE);
            title = chosenArticle.getTitle();
            trail = chosenArticle.getArticleTrail();
            body = chosenArticle.getArticleBody();
            imageUrl = chosenArticle.getThumbnailUrl();
            author = chosenArticle.getAuthor();
            section = chosenArticle.getSection();
            time = chosenArticle.getDate();
            webUrl = chosenArticle.getWebUrl();
        }

        //Set the appropriate texts and image
        title_tv.setText(title);
        trail_tv.setText(Utils.processHtml(trail));
        body_tv.setText(Utils.processHtml(body));
        body_tv.setMovementMethod(LinkMovementMethod.getInstance());
        GlideApp.with(this)
                .load(imageUrl)
                .detailImage()
                .centerCrop()
                .into(details_iv);
        if(!TextUtils.isEmpty(author)){
            author_tv.setText(getString(R.string.byline, author));
        }
        section_tv.setText(section);
        String[] dateAndTime = formatDateTime(time).split("T");
        time_tv.setText(dateAndTime[0] + "\n" + dateAndTime[1]);

        if(savedInstanceState != null){
            final int x = savedInstanceState.getInt(Constants.SCROLL_X);
            final int y = savedInstanceState.getInt(Constants.SCROLL_Y);
            scrollView.post(new Runnable(){
                public void run(){
                    scrollView.scrollTo(x, y);
                }
            });
        }
    }

    private void saveToBookmarks() {
        ContentValues values = new ContentValues();
        values.put(BookmarkEntry.COLUMN_TITLE, title);
        values.put(BookmarkEntry.COLUMN_THUMBNAIL_URL, imageUrl);
        values.put(BookmarkEntry.COLUMN_AUTHOR, author);
        values.put(BookmarkEntry.COLUMN_DATE, time);
        values.put(BookmarkEntry.COLUMN_WEB_URL, webUrl);
        values.put(BookmarkEntry.COLUMN_SECTION, section);
        values.put(BookmarkEntry.COLUMN_TRAIL, trail);
        values.put(BookmarkEntry.COLUMN_BODY, body);
        getContentResolver().insert(BookmarkEntry.CONTENT_URI, values);
        Toast.makeText(this, R.string.article_bookmarked, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_settings:{
                Intent intent = new Intent(DetailsActivity.this, SettingsActivity.class);
                intent.putExtra(Constants.USER_CLICKED_SETTINGS_FROM, DetailsActivity.class.getSimpleName());
                startActivity(intent);
                break;
            }
            case R.id.action_bookmark:{
                saveToBookmarks();
                break;
            }
            case R.id.action_share: {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, webUrl);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
            }
            case R.id.action_bookmarks: {
                Intent intent = new Intent(DetailsActivity.this, BookmarksActivity.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private String formatDateTime(String dateTime){
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sourceFormat.setTimeZone(timeZone);
        Date parsedTime = null;
        try {
            parsedTime = sourceFormat.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TimeZone tz = TimeZone.getDefault();
        SimpleDateFormat destFormat = new SimpleDateFormat("LLL dd, yyyy'T'HH:mm");
        destFormat.setTimeZone(tz);
        return destFormat.format(parsedTime);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.SCROLL_X, scrollView.getScrollX());
        outState.putInt(Constants.SCROLL_Y, scrollView.getScrollY());
    }
}
