package com.example.oya.newsreader.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.ui.ArticleListFragment;

import java.util.ArrayList;
import java.util.Set;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<String> mSections = new ArrayList<>();
    private Context mContext;

    public SectionsPagerAdapter(FragmentManager fm, Set<String> sections, Context context) {
        super(fm);
        mContext = context;
        setPreferredSections(sections);
    }

    public void setPreferredSections(Set<String> sections){
        mSections.clear();
        mSections.addAll(sections);
        Log.d("sectionsAdapter", "setPreferred.." + mSections.size());
        sortSections();
    }

    private void sortSections(){
        ArrayList<String> sort = new ArrayList<>();
        if(mSections.contains(mContext.getString(R.string.politics).toLowerCase()))
            sort.add(mContext.getString(R.string.politics).toLowerCase());
        if(mSections.contains(mContext.getString(R.string.world).toLowerCase()))
            sort.add(mContext.getString(R.string.world).toLowerCase());
        if(mSections.contains(mContext.getString(R.string.business).toLowerCase()))
            sort.add(mContext.getString(R.string.business).toLowerCase());
        if(mSections.contains(mContext.getString(R.string.technology).toLowerCase()))
            sort.add(mContext.getString(R.string.technology).toLowerCase());
        if(mSections.contains(mContext.getString(R.string.science).toLowerCase()))
            sort.add(mContext.getString(R.string.science).toLowerCase());
        if(mSections.contains(mContext.getString(R.string.sport).toLowerCase()))
            sort.add(mContext.getString(R.string.sport).toLowerCase());
        if(mSections.contains(mContext.getString(R.string.football).toLowerCase()))
            sort.add(mContext.getString(R.string.football).toLowerCase());
        if(mSections.contains(mContext.getString(R.string.music).toLowerCase()))
            sort.add(mContext.getString(R.string.music).toLowerCase());
        if(mSections.contains(mContext.getString(R.string.culture).toLowerCase()))
            sort.add(mContext.getString(R.string.culture).toLowerCase());
        if(mSections.contains(mContext.getString(R.string.travel).toLowerCase()))
            sort.add(mContext.getString(R.string.travel).toLowerCase());
        if(mSections.contains(mContext.getString(R.string.fashion).toLowerCase()))
            sort.add(mContext.getString(R.string.fashion).toLowerCase());
        mSections.clear();
        mSections.addAll(sort);
        Log.d("sectionsAdapter", "after sorting" + mSections.size());
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("sectionsAdapter", "getItem is called");
        return ArticleListFragment.newInstance(position, mSections.get(position));
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        Log.d("SectionsAdapter", "inside getCount" +mSections.size());
        for(int i = 0; i < mSections.size() ; i++){
            Log.d("adapter/sections ", "" + mSections.get(i));
        }
        return mSections.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mSections.get(position);
    }

    public ArrayList<String> getSections(){
        return mSections;
    }
}
