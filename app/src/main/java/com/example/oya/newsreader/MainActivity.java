package com.example.oya.newsreader;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<NewsArticle>>, NewsAdapter.ListItemClickListener, SwipeRefreshLayout.OnRefreshListener{

    private NewsAdapter adapter;
    private ArrayList<NewsArticle> articles;
    private static final String LOG_TAG = MainActivity.class.getName();
    private TextView empty_tv;
    RecyclerView recycler;
    LoaderManager loaderManager;
    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setItemAnimator(new DefaultItemAnimator());
        articles = new ArrayList<>();
        adapter = new NewsAdapter(this, articles, this);
        recycler.setAdapter(adapter);
        empty_tv = findViewById(R.id.empty_view);
        refreshLayout = findViewById(R.id.swipe_to_refresh);
        refreshLayout.setOnRefreshListener(this);
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(Constants.NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            empty_tv.setText(R.string.no_connection);
            recycler.setVisibility(View.GONE);
            empty_tv.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public Loader<List<NewsArticle>> onCreateLoader(int id, Bundle args) {
        return new NewsLoader(this, Constants.GUARDIAN_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsArticle>> loader, List<NewsArticle> list) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        refreshLayout.setRefreshing(false);

        if (list != null && !list.isEmpty()) {
            articles.clear();
            articles.addAll(list);
            adapter.notifyDataSetChanged();
        } else {
            //Set empty state text to display "No news found."
            empty_tv.setText(R.string.no_news_found);
            recycler.setVisibility(View.GONE);
            empty_tv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsArticle>> loader) {
    }

    @Override
    public void onListItemClick(View view, int position) {
        switch(view.getId()){
            case R.id.container:{
                openWebPage(position);
                break;
            }
            case R.id.share:{
                shareTheLink(position);
                break;
            }
            case R.id.bookmark:{
                Toast.makeText(this, "bookmark " + position, Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    private void openWebPage(int position){
        Uri uri = Uri.parse(articles.get(position).getWebUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void shareTheLink(int position){
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, articles.get(position).getWebUrl());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onRefresh() {
        loaderManager.restartLoader(Constants.NEWS_LOADER_ID, null, this);
    }
}
