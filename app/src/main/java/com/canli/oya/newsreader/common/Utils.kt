package com.canli.oya.newsreader.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Html
import android.text.Spanned

fun fromHtml(text: String?): Spanned? {
    return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
        @Suppress("DEPRECATION")
        Html.fromHtml(text)
    } else {
        Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
    }
}

fun splitDateAndTime(dateTime : String) : String{
    val parts = dateTime.split("T")
    return "${parts[0]}\n${parts[1]}"
}

/**
 * Utility method for checking availability of internet connection
 */
fun isOnline(context : Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            //for other device how are able to connect with Ethernet
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            //for check internet over Bluetooth
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    } else {
        return connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true
    }
}