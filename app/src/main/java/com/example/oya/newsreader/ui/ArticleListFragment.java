package com.example.oya.newsreader.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.adapters.NewsAdapter;
import com.example.oya.newsreader.data.NewsDbHelper;
import com.example.oya.newsreader.model.NewsArticle;
import com.example.oya.newsreader.utils.Constants;
import com.example.oya.newsreader.utils.SectionLoader;

import java.util.ArrayList;
import java.util.List;

public class ArticleListFragment extends Fragment implements NewsAdapter.ListItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private NewsAdapter adapter;
    private ArrayList<NewsArticle> articles;
    private SwipeRefreshLayout refreshLayout;
    private static final String ARG_SECTION_NAME = "sectionName";
    private String mSection;

    public ArticleListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public static ArticleListFragment newInstance(String sectionName) {
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_NAME, sectionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        RecyclerView recycler = rootView.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setItemAnimator(new DefaultItemAnimator());
        TextView empty_tv = rootView.findViewById(R.id.empty_view);
        refreshLayout = rootView.findViewById(R.id.swipe_to_refresh);
        refreshLayout.setOnRefreshListener(this);
        mSection = getArguments().getString(ARG_SECTION_NAME);
        NewsDbHelper dbHelper = new NewsDbHelper(getActivity(), mSection);
        articles = dbHelper.readFromDatabase(mSection);
        if (!articles.isEmpty()) {
            adapter = new NewsAdapter(getActivity(), articles, this);
            recycler.setAdapter(adapter);
        } else {
            empty_tv.setText(R.string.no_connection);
            recycler.setVisibility(View.GONE);
            empty_tv.setVisibility(View.VISIBLE);
        }
        return rootView;
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
                saveToBookmarks(position);
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

    private void saveToBookmarks(int position) {
        NewsDbHelper dbHelper = new NewsDbHelper(getActivity(), mSection);
        dbHelper.addToBookmarks(articles.get(position));
        Toast.makeText(getActivity(), R.string.article_bookmarked, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(Constants.SYNCH_CHANGED_LOADER_ID, null, new LoaderManager.LoaderCallbacks<List<NewsArticle>>() {
            @Override
            public Loader<List<NewsArticle>> onCreateLoader(int id, Bundle args) {
                return new SectionLoader(getActivity(), mSection);
            }

            @Override
            public void onLoadFinished(Loader<List<NewsArticle>> loader, List<NewsArticle> list) {
                NewsDbHelper dbHelper = new NewsDbHelper(getActivity(), mSection);
                articles = dbHelper.readFromDatabase(mSection);
                refreshLayout.setRefreshing(false);
                if (!articles.isEmpty()) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onLoaderReset(Loader<List<NewsArticle>> loader) {
            }
        });
    }

}
