package com.example.oya.newsreader.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
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

public class DetailsActivity extends AppCompatActivity {

    NewsArticle chosenArticle;

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
        author_tv.setText("By " + chosenArticle.getAuthor());
        section_tv.setText(chosenArticle.getSection());
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
        if (id == R.id.action_settings) {
            Intent intent = new Intent(DetailsActivity.this, SettingsActivity.class);
            intent.putExtra(Constants.USER_CLICKED_SETTINGS_FROM, DetailsActivity.class.getSimpleName());
            startActivity(intent);
        } else if (id == R.id.action_share) {
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, chosenArticle.getWebUrl());
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else if (id == R.id.action_bookmarks){
            Intent intent = new Intent(DetailsActivity.this, BookmarksActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
