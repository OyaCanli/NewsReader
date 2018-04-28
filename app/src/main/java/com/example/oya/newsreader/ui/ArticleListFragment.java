package com.example.oya.newsreader.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.adapters.NewsAdapter;
import com.example.oya.newsreader.model.NewsArticle;
import com.example.oya.newsreader.utils.Constants;
import com.example.oya.newsreader.utils.NewsLoader;

import java.util.ArrayList;
import java.util.List;

public class ArticleListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<NewsArticle>>, NewsAdapter.ListItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private NewsAdapter adapter;
    private ArrayList<NewsArticle> articles;
    private static final String LOG_TAG = MainActivity.class.getName();
    private TextView empty_tv;
    private RecyclerView recycler;
    private LoaderManager loaderManager;
    private SwipeRefreshLayout refreshLayout;
    private View loadingIndicator;
    private ImageButton retryButton;
    private static final String ARG_SECTION_NAME = "sectionName";
    private static final String ARG_SECTION_NUMBER = "sectionNumber";
    private int loaderId;

    public ArticleListFragment() {
    }

    public static ArticleListFragment newInstance(int sectionNumber, String sectionName) {
        ArticleListFragment fragment = new ArticleListFragment();
        Log.d("ListFragment", "inside newInstance" + sectionName);
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_SECTION_NAME, sectionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d("ListFragment", "OnCreateView is called");
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        recycler = rootView.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setItemAnimator(new DefaultItemAnimator());
        articles = new ArrayList<>();
        adapter = new NewsAdapter(getContext(), articles, this);
        recycler.setAdapter(adapter);
        empty_tv = rootView.findViewById(R.id.empty_view);
        refreshLayout = rootView.findViewById(R.id.swipe_to_refresh);
        refreshLayout.setOnRefreshListener(this);
        loadingIndicator = rootView.findViewById(R.id.loading_indicator);
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

    private boolean thereIsConnection() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        return networkInfo != null && networkInfo.isConnected();
    }

    private void startLoader() {
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
        Log.d("ListFragment", "onCreateLoader" + getArguments().getString(ARG_SECTION_NAME));
        return new NewsLoader(getActivity(), getArguments().getString(ARG_SECTION_NAME));
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
            if (thereIsConnection()) empty_tv.setText(R.string.no_news_found);
            else empty_tv.setText(R.string.no_connection);
            recycler.setVisibility(View.GONE);
            empty_tv.setVisibility(View.VISIBLE);
            if (retryButton != null) retryButton.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onLoaderReset(@NonNull android.content.Loader<List<NewsArticle>> loader) {
    }

    @Override
    public void onListItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.container: {
                openDetails(position);
                break;
            }
            case R.id.share: {
                shareTheLink(position);
                break;
            }
            case R.id.bookmark: {
                Toast.makeText(getActivity(), "bookmark " + position, Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    private void openDetails(int position) {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(Constants.CHOSEN_ARTICLE, articles.get(position));
        startActivity(intent);
    }

    private void shareTheLink(int position) {
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

    private void restartLoading() {
        if (loaderManager == null) {
            loaderManager = getActivity().getLoaderManager();
            loaderManager.initLoader(loaderId, null, this);
        } else {
            loaderManager.restartLoader(loaderId, null, this);
        }
    }
}
