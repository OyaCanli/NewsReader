package com.example.oya.newsreader.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.oya.newsreader.ui.newslist.ArticleListFragment

const val SECTION_KEY = "sectionKey"

class SectionsPagerAdapter(activity: FragmentActivity, val sections : List<String>) : FragmentStateAdapter(activity) {

    override fun getItemCount() = sections.size

    override fun createFragment(position: Int): Fragment {
        val args = Bundle()
        args.putString(SECTION_KEY, sections[position])
        val frag = ArticleListFragment()
        frag.arguments = args
        return frag
    }
}