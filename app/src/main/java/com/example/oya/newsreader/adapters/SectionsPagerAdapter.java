package com.example.oya.newsreader.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.example.oya.newsreader.ui.ArticleListFragment;

import java.util.ArrayList;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    private static ArrayList<String> mSections;
    private final Fragment[] mFragments;

    public SectionsPagerAdapter(FragmentManager fm, ArrayList<String> sections) {
        super(fm);
        mSections = sections;
        mFragments = new Fragment[mSections.size()];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object ret = super.instantiateItem(container, position);
        mFragments[position] = (Fragment) ret;
        return ret;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("SectionsAdapter", "getItem is called");
        Fragment frag = mFragments[position];
        if (frag == null) {
            Log.d("SectionsAdapter", "frag is null");
            frag = ArticleListFragment.newInstance(position, mSections.get(position));
            mFragments[position] = frag;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return mSections.size();
    }

}
