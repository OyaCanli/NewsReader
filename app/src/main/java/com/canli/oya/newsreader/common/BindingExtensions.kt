package com.canli.oya.newsreader.common

import android.view.View
import com.canli.oya.newsreader.databinding.ActivityListBinding
import com.canli.oya.newsreader.databinding.FragmentListBinding

fun FragmentListBinding.showLoading() {
    loadingIndicator.visibility = View.VISIBLE
    recycler.visibility = View.GONE
}

fun FragmentListBinding.showList() {
    if(recycler.visibility != View.VISIBLE){
        recycler.visibility = View.VISIBLE
    }
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