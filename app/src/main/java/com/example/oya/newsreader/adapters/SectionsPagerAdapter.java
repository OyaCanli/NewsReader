package com.example.oya.newsreader.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.oya.newsreader.ui.ArticleListFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return ArticleListFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return 5;
    }
}
