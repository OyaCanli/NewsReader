package com.example.oya.newsreader.common;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

public final class Utils {

    public static Spanned processHtml(String text){
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            return Html.fromHtml(text);
        } else {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        }
    }
}
