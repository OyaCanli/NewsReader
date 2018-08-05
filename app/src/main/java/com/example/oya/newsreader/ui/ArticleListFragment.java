package com.example.oya.newsreader.ui;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.adapters.NewsCursorAdapter;
import com.example.oya.newsreader.data.NewsContract;
import com.example.oya.newsreader.data.NewsContract.NewsEntry;
import com.example.oya.newsreader.synch.SyncTask;

import static com.example.oya.newsreader.data.NewsContract.BASE_CONTENT_URI;

public class ArticleListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, NewsCursorAdapter.ListItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private NewsCursorAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private static final String ARG_SECTION_NAME = "sectionName";
    private static final String ARG_SECTION_NUMBER = "sectionNumber";
    private String mSection;
    private ProgressBar mLoadingIndicator;
    private RecyclerView recycler;
    private TextView empty_tv;
    private int loader_id;
    private LoaderManager.LoaderCallbacks<Cursor> mCallBack;

    public ArticleListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public static ArticleListFragment newInstance(int position, String sectionName) {
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_NAME, sectionName);
        args.putInt(ARG_SECTION_NUMBER, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        recycler = rootView.findViewById(R.id.recycler);
        boolean isTablet = getActivity().getResources().getBoolean(R.bool.isTablet);
        if(isTablet){
            recycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else{
            recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        recycler.setItemAnimator(new DefaultItemAnimator());
        refreshLayout = rootView.findViewById(R.id.swipe_to_refresh);
        refreshLayout.setOnRefreshListener(this);
        mLoadingIndicator = rootView.findViewById(R.id.loading_indicator);
        empty_tv = rootView.findViewById(R.id.empty_view);
        mSection = getArguments().getString(ARG_SECTION_NAME);
        loader_id = getArguments().getInt(ARG_SECTION_NUMBER);
        adapter = new NewsCursorAdapter(getActivity(),this);
        recycler.setAdapter(adapter);
        mCallBack = this;
        showLoading();
        getLoaderManager().initLoader(loader_id, null, mCallBack);

        return rootView;
    }

    private void showLoading() {
        recycler.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showData() {
        recycler.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.GONE);
        empty_tv.setVisibility(View.GONE);
    }

    @Override
    public void onListItemClick(View view, long id) {
        switch (view.getId()) {
            case R.id.article_item_root: {
                openDetails(id);
                break;
            }
            case R.id.share: {
                shareTheLink(id);
                break;
            }
            case R.id.bookmark: {
                saveToBookmarks(id);
                break;
            }
        }
    }

    private Uri getUriById(long id){
        Uri content_uri = BASE_CONTENT_URI.buildUpon().appendPath(mSection).build();
        return ContentUris.withAppendedId(content_uri, id);
    }

    private void openDetails(long id) {
        Uri uri = getUriById(id);
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.setData(uri);
        startActivity(intent);
    }

    private void shareTheLink(long id) {
        //Using the id at hand, find the webUrl of the article
        Uri uri = getUriById(id);
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null );
        cursor.moveToFirst();
        String webUrl = cursor.getString(cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_WEB_URL));
        cursor.close();
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, webUrl);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void saveToBookmarks(long id) {
        //First get the info related to the article from the corresponding table and put them in a ContentValues object
        Uri uri = getUriById(id);
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null );
        cursor.moveToFirst();
        ContentValues values = new ContentValues();
        values.put(NewsEntry.COLUMN_TITLE, cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_TITLE)));
        values.put(NewsEntry.COLUMN_THUMBNAIL_URL, cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_THUMBNAIL_URL)));
        values.put(NewsEntry.COLUMN_AUTHOR, cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_AUTHOR)));
        values.put(NewsEntry.COLUMN_DATE, cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_DATE)));
        values.put(NewsEntry.COLUMN_WEB_URL, cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_WEB_URL)));
        values.put(NewsEntry.COLUMN_SECTION, cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_SECTION)));
        values.put(NewsEntry.COLUMN_TRAIL, cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_TRAIL)));
        values.put(NewsEntry.COLUMN_BODY, cursor.getString(cursor.getColumnIndex(NewsEntry.COLUMN_BODY)));
        //Then save it to bookmarks table
        getActivity().getContentResolver().insert(uri, values);
        Toast.makeText(getActivity(), R.string.article_bookmarked, Toast.LENGTH_SHORT).show();
        cursor.close();
    }

    @Override
    public void onRefresh() {
        //Start fetching new data, wait two seconds and restart loading from database
        SyncTask.startImmediateSync(getActivity());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getLoaderManager().restartLoader(loader_id, null, mCallBack);
            }
        }, 2000);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        //Uri depends on the section, that's why I create uri dynamically.
        Uri section_uri = BASE_CONTENT_URI.buildUpon().appendPath(mSection).build();
        Log.d("ListFragment", "onCreateLoader is called");
        return new CursorLoader(getActivity(),
                section_uri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mLoadingIndicator.setVisibility(View.GONE);
        refreshLayout.setRefreshing(false);
        Log.d("ListFragment", "onLoadFinish is called");
        if(data != null){
            if (data.getCount() != 0) {
                adapter.swapCursor(data);
                showData();
            } else {
                empty_tv.setText(R.string.no_connection);
                recycler.setVisibility(View.GONE);
                empty_tv.setVisibility(View.VISIBLE);
            }
        }
        else {
            empty_tv.setText(R.string.no_connection);
            recycler.setVisibility(View.GONE);
            empty_tv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d("ListFragment", "onLoaderReset is called");
    }
}
