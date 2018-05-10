package com.example.oya.newsreader.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.example.oya.newsreader.R;
import com.example.oya.newsreader.model.NewsArticle;
import com.example.oya.newsreader.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DetailsActivity extends AppCompatActivity {

    private NewsArticle chosenArticle;

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
        TextView title_tv = findViewById(R.id.details_title);
        TextView trail_tv = findViewById(R.id.details_trail);
        TextView body_tv = findViewById(R.id.details_body);
        ImageView details_iv = findViewById(R.id.details_image);
        TextView author_tv = findViewById(R.id.details_author);
        TextView section_tv = findViewById(R.id.details_section);
        TextView time_tv = findViewById(R.id.details_time);
        //Get the chosen article info from the bundle
        Bundle bundle = getIntent().getExtras();
        chosenArticle = bundle.getParcelable(Constants.CHOSEN_ARTICLE);
        //Set the appropriate texts and image
        title_tv.setText(chosenArticle.getTitle());
        trail_tv.setText(Html.fromHtml(chosenArticle.getArticleTrail()));
        body_tv.setText(Html.fromHtml(chosenArticle.getArticleBody()));
        Glide.with(this)
                .load(chosenArticle.getThumbnailUrl())
                .into(details_iv);
        if(!TextUtils.isEmpty(chosenArticle.getAuthor())){
            author_tv.setText(getString(R.string.byline, chosenArticle.getAuthor()));
        }
        section_tv.setText(chosenArticle.getSection());
        String[] dateAndTime = formatDateTime(chosenArticle.getDate()).split("T");
        time_tv.setText(dateAndTime[0] + "\n" + dateAndTime[1]);
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
            case R.id.action_share: {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, chosenArticle.getWebUrl());
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
}
