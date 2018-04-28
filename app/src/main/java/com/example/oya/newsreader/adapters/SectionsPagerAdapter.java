package com.example.oya.newsreader.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.example.oya.newsreader.ui.ArticleListFragment;

import java.util.ArrayList;
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
        sortSections();
    }

    private void sortSections(){
        ArrayList<String> sort = new ArrayList<>();
        if(mSections.contains("politics")) sort.add("politics");
        if(mSections.contains("world")) sort.add("world");
        if(mSections.contains("business")) sort.add("business");
        if(mSections.contains("technology")) sort.add("technology");
        if(mSections.contains("science")) sort.add("science");
        if(mSections.contains("sport")) sort.add("sport");
        if(mSections.contains("football")) sort.add("football");
        if(mSections.contains("music")) sort.add("music");
        if(mSections.contains("culture")) sort.add("culture");
        if(mSections.contains("travel")) sort.add("travel");
        if(mSections.contains("fashion")) sort.add("fashion");
        mSections.clear();
        mSections.addAll(sort);
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
