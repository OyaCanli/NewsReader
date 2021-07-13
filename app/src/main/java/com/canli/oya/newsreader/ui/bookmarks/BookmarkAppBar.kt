package com.canli.oya.newsreader.ui.bookmarks

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.canli.oya.newsreader.R
import com.canli.oya.newsreader.ui.main.OverflowMenu
import com.canli.oya.newsreader.ui.main.SettingsDropDownItem
import com.canli.oya.newsreader.ui.main.UpButton


@Composable
fun BookmarkAppBar(onSettingsClicked: () -> Unit, onUpClicked: () -> Unit) {
    TopAppBar(
        title = {
            Text(text = stringResource(R.string.bookmark))
        },
        navigationIcon = {
            UpButton(onUpClicked = onUpClicked)
        },
        actions = {
            OverflowMenu {
                SettingsDropDownItem(onClick = onSettingsClicked)
            }
        }
    )
}

