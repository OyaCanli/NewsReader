package com.canli.oya.newsreader.ui.main


import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.canli.oya.newsreader.ui.theme.NewsReaderTheme

@Composable
fun MainScreen(
    topAppBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    NewsReaderTheme {
        Scaffold(
            topBar = topAppBar

        ) { // A surface container using the 'background' color from the theme
            Surface(
                color = MaterialTheme.colors.background,
            ) {
                content()
            }
        }
    }
}
