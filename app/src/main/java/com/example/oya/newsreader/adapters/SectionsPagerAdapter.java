package com.example.oya.newsreader.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.oya.newsreader.ui.ArticleListFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<String> mSections = new ArrayList<>();

    public SectionsPagerAdapter(FragmentManager fm, Set<String> sections) {
        super(fm);
        setPreferredSections(sections);
    }

    public void setPreferredSections(Set<String> sections){
        mSections.clear();
        mSections.addAll(sections);
        Collections.sort(mSections);
        Collections.reverse(mSections);
    }

    @Override
    public Fragment getItem(int position) {
        return ArticleListFragment.newInstance(position, mSections.get(position));
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        Log.d("SectionsAdapter", "" +mSections.size());
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
