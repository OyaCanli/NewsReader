package com.example.oya.newsreader;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ArticleListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<NewsArticle>>, NewsAdapter.ListItemClickListener, SwipeRefreshLayout.OnRefreshListener{

    private NewsAdapter adapter;
    private ArrayList<NewsArticle> articles;
    private static final String LOG_TAG = MainActivity.class.getName();
    private TextView empty_tv;
    RecyclerView recycler;
    LoaderManager loaderManager;
    SwipeRefreshLayout refreshLayout;
    View loadingIndicator;
    ImageButton retryButton;
    private static final String ARG_SECTION_NUMBER = "section_number";
    int loaderId;

    public ArticleListFragment(){
    }

    static ArticleListFragment newInstance(int sectionNumber) {
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.article_list, container, false);
        recycler = rootView.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setItemAnimator(new DefaultItemAnimator());
        articles = new ArrayList<>();
        adapter = new NewsAdapter(getActivity(), articles, this);
        recycler.setAdapter(adapter);
        empty_tv = rootView.findViewById(R.id.empty_view);
        refreshLayout = rootView.findViewById(R.id.swipe_to_refresh);
        refreshLayout.setOnRefreshListener(this);
        loadingIndicator = rootView.findViewById(R.id.loading_indicator);
        // Get a reference to the ConnectivityManager to check state of network connectivity
        retryButton = rootView.findViewById(R.id.retry);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoader();
            }
        });
        startLoader();
        return rootView;
    }

    private boolean thereIsConnection(){
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        return networkInfo != null && networkInfo.isConnected();
    }

    private void startLoader(){
        if (thereIsConnection()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            loadingIndicator.setVisibility(View.VISIBLE);
            loaderManager = getActivity().getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderId = getArguments().getInt(ARG_SECTION_NUMBER);
            loaderManager.initLoader(loaderId, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            empty_tv.setText(R.string.no_connection);
            recycler.setVisibility(View.GONE);
            empty_tv.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Loader<List<NewsArticle>> onCreateLoader(int id, Bundle args) {
        String section;
        switch (id){
            case 1:{
                section = "politics";
                break;
            }
            case 2:{
                section = "world";
                break;
            }
            case 3:{
                section = "business";
                break;
            }
            case 4:{
                section = "technology";
                break;
            }
            case 5:{
                section = "science";
                break;
            }
            default:{
                section = "politics";
            }
        }
        String request_url = Constants.BASE_URL + section + Constants.END_QUERY;
        return new NewsLoader(getActivity(), request_url);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsArticle>> loader, List<NewsArticle> list) {
        loadingIndicator.setVisibility(View.GONE);
        refreshLayout.setRefreshing(false);


        if (list != null && !list.isEmpty()) {
            articles.clear();
            articles.addAll(list);
            adapter.notifyDataSetChanged();
        } else {
            if(thereIsConnection()){
                //Set empty state text to display "No news found."
                empty_tv.setText(R.string.no_news_found);
                recycler.setVisibility(View.GONE);
                empty_tv.setVisibility(View.VISIBLE);
                if(retryButton != null) retryButton.setVisibility(View.GONE);
            } else {
                empty_tv.setText(R.string.no_connection);
                recycler.setVisibility(View.GONE);
                empty_tv.setVisibility(View.VISIBLE);
                if(retryButton != null) retryButton.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onLoaderReset(@NonNull android.content.Loader<List<NewsArticle>> loader) {
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
                Toast.makeText(getActivity(), "bookmark " + position, Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    private void openWebPage(int position){
        Uri uri = Uri.parse(articles.get(position).getWebUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if(intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void shareTheLink(int position){
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, articles.get(position).getWebUrl());
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onRefresh() {
        startLoader();
    }

    private void restartLoading(){
        if(loaderManager == null){
            loaderManager = getActivity().getLoaderManager();
            loaderManager.initLoader(loaderId, null, this);
        } else{
            loaderManager.restartLoader(loaderId, null, this);
        }
    }
}
