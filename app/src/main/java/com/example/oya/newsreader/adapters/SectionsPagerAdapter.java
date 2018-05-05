package com.example.oya.newsreader.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.ui.ArticleListFragment;
import com.example.oya.newsreader.ui.SettingsActivity;
import com.example.oya.newsreader.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    private static ArrayList<String> mSections;
    private Context mContext;
    private Fragment[] mFragments;

    public SectionsPagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        mContext = context;
        mSections = getSections();
        mFragments = new Fragment[mSections.size()];
    }

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

    @Override
    public CharSequence getPageTitle(int position) {
        return mSections.get(position);
    }

    public ArrayList<String> getSections(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        Set<String> default_sections = new HashSet<>(Arrays.asList(mContext.getResources().getStringArray(R.array.pref_section_default_values)));
        Set<String> orderedSections = sharedPreferences.getStringSet(Constants.PREF_SORTED_SECTIONS, SettingsActivity.sortInDefaultOrder(default_sections));
        return new ArrayList<>(orderedSections);
    }
}
