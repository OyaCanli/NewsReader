package com.canli.oya.newsreader.common

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Html
import android.text.Spanned
import timber.log.Timber

fun fromHtml(text: String): String {
    return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
        @Suppress("DEPRECATION")
        Html.fromHtml(text).toString()
    } else {
        Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString()
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

fun shareTheLink(context : Context, webUrl: String) {
    Timber.d("share link is clicked")
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, webUrl)
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}