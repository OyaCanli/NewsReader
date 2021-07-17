package com.canli.oya.newsreader.ui.main

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.canli.oya.newsreader.R
import com.canli.oya.newsreader.ui.bookmarks.BookmarkActivity
import com.canli.oya.newsreader.ui.settings.SettingsActivity

@Composable
fun UpButton(onUpClicked: () -> Unit) {
    IconButton(onClick = onUpClicked) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = stringResource(R.string.cd_home_btn),
            tint = Color.White
        )
    }
}

@Composable
fun SettingsDropDownItem() {
    val context = LocalContext.current
    DropdownMenuItem(onClick = { launchSettings(context) }) {
        Icon(
            Icons.Filled.Settings,
            contentDescription = stringResource(R.string.settings),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(R.string.settings))
    }
}

private fun launchSettings(context : Context) {
    val intent = Intent(context, SettingsActivity::class.java)
    context.startActivity(intent)
}

@Composable
fun BookmarksDropDownItem() {
    val context = LocalContext.current
    DropdownMenuItem(onClick = { launchBookmarks(context) }) {
        Icon(
            painter = painterResource(R.drawable.ic_bookmark_filled),
            contentDescription = stringResource(R.string.bookmark),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(R.string.bookmark))
    }
}

private fun launchBookmarks(context : Context) {
    val intent = Intent(context, BookmarkActivity::class.java)
    context.startActivity(intent)
}

@Composable
fun OverflowMenu(content: @Composable () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    IconButton(onClick = {
        showMenu = !showMenu
    }) {
        Icon(
            imageVector = Icons.Outlined.MoreVert,
            contentDescription = stringResource(R.string.more),
            tint = Color.White
        )
    }
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
    ) {
        content()
    }
}