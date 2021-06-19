package com.example.oya.newsreader.common

import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.net.toUri
import coil.load
import com.example.oya.newsreader.R

var toast : Toast? = null

fun Context.shortToast(message: String) {
    toast?.cancel()
    toast = Toast.makeText(this, message, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

fun Context.shortToast(@StringRes message: Int) {
    toast?.cancel()
    toast = Toast.makeText(this, message, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

fun Context.longToast(message: String) {
    toast?.cancel()
    toast = Toast.makeText(this, message, Toast.LENGTH_LONG).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

fun Context.longToast(@StringRes message: Int) {
    toast?.cancel()
    toast = Toast.makeText(this, message, Toast.LENGTH_LONG).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

/**
 * BindingAdapter for loading images with Coil library
 */
fun ImageView.bindImage(imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl
            .toUri()
            .buildUpon()
            .scheme("https")
            .build()
        this.load(imgUri) {
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }
    }
}