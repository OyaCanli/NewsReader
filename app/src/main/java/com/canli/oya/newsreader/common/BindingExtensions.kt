package com.canli.oya.newsreader.common

import android.view.View
import androidx.annotation.StringRes
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
    emptyView.visibility = View.GONE
    loadingIndicator.visibility = View.VISIBLE
    recycler.visibility = View.GONE
}

fun ActivityListBinding.showList() {
    emptyView.visibility = View.GONE
    recycler.visibility = View.VISIBLE
    loadingIndicator.visibility = View.GONE
}

fun ActivityListBinding.showEmpty(@StringRes text : Int) {
    emptyView.visibility = View.VISIBLE
    emptyView.setText(text)
    recycler.visibility = View.GONE
    loadingIndicator.visibility = View.GONE
}