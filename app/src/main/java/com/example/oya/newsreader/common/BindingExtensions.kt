package com.example.oya.newsreader.common

import android.view.View
import com.example.oya.newsreader.databinding.ActivityListBinding
import com.example.oya.newsreader.databinding.FragmentListBinding

fun FragmentListBinding.showLoading() {
    loadingIndicator.visibility = View.VISIBLE
    recycler.visibility = View.GONE
}

fun FragmentListBinding.showList() {
    recycler.visibility = View.VISIBLE
    loadingIndicator.visibility = View.GONE
}

fun ActivityListBinding.showLoading() {
    loadingIndicator.visibility = View.VISIBLE
    recycler.visibility = View.GONE
}

fun ActivityListBinding.showList() {
    recycler.visibility = View.VISIBLE
    loadingIndicator.visibility = View.GONE
}