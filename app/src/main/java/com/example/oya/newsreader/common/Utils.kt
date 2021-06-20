package com.example.oya.newsreader.common

import android.os.Build
import android.text.Html
import android.text.Spanned

fun fromHtml(text: String?): Spanned? {
    return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
        Html.fromHtml(text)
    } else {
        Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
    }
}